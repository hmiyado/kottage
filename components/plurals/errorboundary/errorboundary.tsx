import React, { ErrorInfo, ReactNode } from 'react'
import Button from '../../pieces/button/button'

interface ErrorBoundaryProps {
  children: ReactNode
  fallback?: (error: Error, resetError: () => void) => ReactNode
}

interface ErrorBoundaryState {
  error: Error | null
}

export default class ErrorBoundary extends React.Component<
  ErrorBoundaryProps,
  ErrorBoundaryState
> {
  constructor(props: ErrorBoundaryProps) {
    super(props)
    this.state = { error: null }
  }

  static getDerivedStateFromError(error: Error) {
    return { error }
  }

  componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    if (!this.props.fallback) {
      console.error(`error: ${JSON.stringify(error)}`)
      console.error(`errorInfo: ${errorInfo.componentStack}`)
    }
  }

  render() {
    const error = this.state.error
    if (!error) {
      return this.props.children
    }

    const resetError = () => this.setState({ error: null })
    const fallback = this.props.fallback
    const defaultComponent = (
      <div>
        <h1>エラーが発生しました。</h1>
        <Button text="OK" onClick={resetError}></Button>
      </div>
    )
    return fallback ? fallback(error, resetError) : defaultComponent
  }
}
