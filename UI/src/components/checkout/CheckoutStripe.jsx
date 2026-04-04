import { Elements, PaymentElement, useElements, useStripe } from '@stripe/react-stripe-js'
import { loadStripe } from '@stripe/stripe-js'
import { useState } from 'react'

const pk = import.meta.env.VITE_STRIPE_PUBLISHABLE_KEY
const stripePromise = pk ? loadStripe(pk) : null

function Inner({ clientSecret, onSuccess }) {
  const stripe = useStripe()
  const elements = useElements()
  const [err, setErr] = useState('')
  const [busy, setBusy] = useState(false)

  async function pay(e) {
    e.preventDefault()
    if (!stripe || !elements) return
    setErr('')
    setBusy(true)
    try {
      const { error } = await stripe.confirmPayment({
        elements,
        confirmParams: {
          return_url: window.location.href,
        },
        redirect: 'if_required',
      })
      if (error) {
        setErr(error.message || 'Payment failed')
      } else {
        onSuccess?.()
      }
    } finally {
      setBusy(false)
    }
  }

  return (
    <form onSubmit={pay}>
      <PaymentElement />
      {err && <div className="err">{err}</div>}
      <button type="submit" disabled={!stripe || busy}>
        {busy ? 'Processing…' : 'Pay'}
      </button>
    </form>
  )
}

export function CheckoutStripe({ clientSecret, onSuccess }) {
  if (!stripePromise) {
    return <p className="warn">Set VITE_STRIPE_PUBLISHABLE_KEY to pay with card.</p>
  }
  return (
    <Elements stripe={stripePromise} options={{ clientSecret }}>
      <Inner clientSecret={clientSecret} onSuccess={onSuccess} />
    </Elements>
  )
}
