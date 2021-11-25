import Entry from 'components/entry/entry'
import TwoColumn from 'components/template/twocolumn/twocolumn'

export default function EntriesSerialNumberPage() {
  return (
    <TwoColumn>
      <>
        <Entry title="title" time="time" author="author">
          body
        </Entry>
      </>
    </TwoColumn>
  )
}
