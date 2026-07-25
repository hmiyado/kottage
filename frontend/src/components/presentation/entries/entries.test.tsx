import { afterEach, describe, expect, test, vi } from 'vitest'
import { cleanup, fireEvent, render, screen } from '@testing-library/react'
import Entries from './entries'
import { EntryProps } from '../../plurals/entry/entry'
import UserContext from '../../../context/user'
import EntryRepository from '../../../repository/entry/entryRepository'

vi.mock('../../plurals/comment/commentlist/commentlist', () => ({
  default: () => <></>,
}))

vi.mock('../../../repository/entry/entryRepository', () => ({
  default: {
    deleteEntry: vi.fn(),
  },
}))

const entry: EntryProps = {
  serialNumber: 1,
  title: 'title',
  body: 'body',
  time: '2021-11-23T23:31:20+09:00',
  author: 'name',
  commentsCount: 0,
}

const deleteButton = () => screen.queryByText('削除する')

afterEach(() => {
  cleanup()
  vi.clearAllMocks()
})

describe('Entries', () => {
  test('should not show delete button when not signed in', () => {
    render(
      <UserContext.Provider value={{ user: null, updateUser: () => {} }}>
        <Entries entry={entry} />
      </UserContext.Provider>,
    )
    expect(deleteButton()).toBeNull()
  })

  test('should show delete button when signed in', () => {
    render(
      <UserContext.Provider
        value={{
          user: { id: 1, screenName: 'admin', accountLinks: [] },
          updateUser: () => {},
        }}
      >
        <Entries entry={entry} />
      </UserContext.Provider>,
    )
    expect(deleteButton()).not.toBeNull()
  })

  test('should delete entry when delete button is clicked and confirmed', () => {
    vi.spyOn(window, 'confirm').mockReturnValue(true)
    render(
      <UserContext.Provider
        value={{
          user: { id: 1, screenName: 'admin', accountLinks: [] },
          updateUser: () => {},
        }}
      >
        <Entries entry={entry} />
      </UserContext.Provider>,
    )
    fireEvent.click(deleteButton()!)
    expect(EntryRepository.deleteEntry).toHaveBeenCalledWith(1)
  })

  test('should not delete entry when delete is not confirmed', () => {
    vi.spyOn(window, 'confirm').mockReturnValue(false)
    render(
      <UserContext.Provider
        value={{
          user: { id: 1, screenName: 'admin', accountLinks: [] },
          updateUser: () => {},
        }}
      >
        <Entries entry={entry} />
      </UserContext.Provider>,
    )
    fireEvent.click(deleteButton()!)
    expect(EntryRepository.deleteEntry).not.toHaveBeenCalled()
  })
})
