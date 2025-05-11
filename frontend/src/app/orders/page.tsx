'use client'

import { useEffect, useState } from 'react'
import { useRouter } from 'next/navigation'
import { useAuth } from '@/context/AuthContext'
import Link from 'next/link'
import { api } from '@/services/api'

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

export default function OrdersPage() {
  const router = useRouter()
  const { token } = useAuth()
  const [orders, setOrders] = useState<Order[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    if (!token) {
      router.push('/login')
      return
    }
    api.get<Order[]>('/cart/orders', {
      headers: { Authorization: `Bearer ${token}` }
    })
      .then(res => setOrders(res.data))
      .catch(err => {
        console.error(err)
        setError('No se pudieron cargar las órdenes')
      })
      .finally(() => setLoading(false))
  }, [token])

  if (loading) return <p className="p-4">Cargando órdenes…</p>
  if (error)   return <p className="p-4 text-red-600">{error}</p>
  if (!orders.length) return <p className="p-4">No hay órdenes.</p>

  return (
    <div className="p-6">
      <h1 className="text-2xl mb-4">Mis Órdenes</h1>
      <ul className="space-y-3">
        {orders.map(order => {
          const createdDate = new Date(order.createdAt).toLocaleString()
          const total = order.items
            .reduce((sum, i) => sum + Number(i.product.price) * i.quantity, 0)
            .toLocaleString()
          return (
            <li key={order.id} className="border rounded p-4 flex justify-between items-center">
              <div>
                <p className="font-semibold">Pedido #{order.id}</p>
                <p className="text-sm text-gray-600">Fecha: {createdDate}</p>
                <p className="text-sm">Total: Gs. {total}</p>
              </div>
              <Link
                href={`/orders/${order.id}`}
                className="text-blue-600 hover:underline"
              >
                Ver detalles
              </Link>
            </li>
          )
        })}
      </ul>
    </div>
  )
}