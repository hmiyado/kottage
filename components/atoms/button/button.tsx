export default function Button({
  text,
  icon,
  ...attributes
}: {
  text: string
  icon: string | null
} & React.ButtonHTMLAttributes<HTMLButtonElement>) {
  return <button {...attributes}>{text}</button>
}
