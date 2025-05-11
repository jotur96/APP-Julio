'use client'

import { useParams, useRouter } from 'next/navigation'
import { useEffect, useState } from 'react'
import { api } from '@/services/api'
import { useAuth } from '@/context/AuthContext'

interface OrderItem {
  product: {
    id: number
    name: string
    description: string
    price: number
    imageUrl: string
  }
  quantity: number
}

interface Order {
  id: number
  createdAt: string
  items: OrderItem[]
}

export default function OrderPage() {
  const { orderId } = useParams() as { orderId: string }
  const router = useRouter()
  const { token } = useAuth()
  const [order, setOrder] = useState<Order | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    // Redirige si no hay token
    if (!token) {
      router.push('/login')
      return
    }
    if (!orderId) return

    // Petición con token en header
    api.get<Order>(`/cart/orders/${orderId}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
      .then(res => setOrder(res.data))
      .catch(err => {
        console.error(err)
        setError('No se pudo cargar el pedido')
      })
      .finally(() => setLoading(false))
  }, [orderId, token])

  if (loading) return <p className="p-4">Cargando pedido…</p>
  if (error) return <p className="p-4 text-red-600">{error}</p>
  if (!order) return <p className="p-4">Pedido no encontrado.</p>

  const createdDate = new Date(order.createdAt).toLocaleString()
  const total = order.items
    .reduce((sum, i) => sum + Number(i.product.price) * i.quantity, 0)
    .toLocaleString()

  return (
    <div className="p-6">
      <h1 className="text-2xl mb-1">Pedido #{order.id}</h1>
      <p className="text-sm text-gray-600 mb-4">Fecha: {createdDate}</p>
      <ul className="space-y-4">
        {order.items.map(({ product, quantity }) => {
          const lineTotal = (Number(product.price) * quantity).toLocaleString()
          return (
            <li key={product.id} className="flex items-center space-x-4">
              <img
                src={product.imageUrl}
                alt={product.name}
                className="h-16 w-16 object-cover rounded"
              />
              <div className="flex-1">
                <p className="font-semibold">{product.name}</p>
                <p>Cantidad: {quantity}</p>
              </div>
              <p className="font-bold">Gs. {lineTotal}</p>
            </li>
          )
        })}
      </ul>

      <div className="mt-6 flex justify-between items-center">
        <p className="text-xl font-bold">Total: Gs. {total}</p>
        <button
          onClick={() => router.push('/')}
          className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
        >
          Volver a productos
        </button>
      </div>
    </div>
  )
}
