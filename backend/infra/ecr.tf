resource "aws_ecr_repository" "kottage" {
  name = "kottage"

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Name    = "kottage"
    Service = "kottage"
  }
}

resource "aws_ecr_lifecycle_policy" "kottage" {
  repository = aws_ecr_repository.kottage.name

  # 2ルール構成。tagStatus="tagged"とtagStatus="untagged"は互いに排他的な集合を選択する
  # ため、ECRの評価順序に厳密な意味はない（tagStatus="any"を混在させる場合のみ、
  # それを最後（最大のrulePriority）にする必要がある）。ここでは可読性のため1→2の
  # 昇順で並べる。
  policy = jsonencode({
    rules = [
      {
        rulePriority = 1
        description  = "Keep only the last 10 tagged images"
        selection = {
          tagStatus      = "tagged"
          tagPatternList = ["*"]
          countType      = "imageCountMoreThan"
          countNumber    = 10
        }
        action = {
          type = "expire"
        }
      },
      {
        rulePriority = 2
        # multi-archのbuildxはタグをイメージインデックスにのみ付け、個別アーキテクチャ
        # のマニフェストとattestationはuntaggedのまま残る（1回のpushで3〜4個生成される）。
        # 上のルールはtagStatus="tagged"のみを対象にしているため、これらは世代管理の
        # 対象外のまま無期限に蓄積していた。
        #
        # 注意: ECRのライフサイクルポリシーはマニフェストリストの参照関係を認識しない。
        # 短い経過日数（例: 1日）で期限切れにすると、直近pushの個別アーキマニフェストは
        # まだ有効なタグ付きイメージから参照されているにもかかわらず削除されてしまう
        # おそれがある。そのため時間ベースではなく件数ベースにし、上のタグ付き保持数
        # （10世代）と同じオーダーの十分なバッファ（1世代あたり最大4個 × 10世代 = 40）
        # を確保する。
        description = "Keep only the last 40 untagged images (per-arch manifests/attestations from multi-arch pushes)"
        selection = {
          tagStatus   = "untagged"
          countType   = "imageCountMoreThan"
          countNumber = 40
        }
        action = {
          type = "expire"
        }
      }
    ]
  })
}
