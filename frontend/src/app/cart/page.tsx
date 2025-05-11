'use client'

import { useEffect, useState } from 'react'
import { useRouter } from 'next/navigation'
import { useAuth } from '@/context/AuthContext'
import { useCart } from '@/context/CartContext'

export default function CarritoPage() {
  const { token } = useAuth()
  const router = useRouter()
  const { items, loading, error, removeItem, clearCart, checkout, fetchCart, addItem, updateItemQuantity } = useCart()

  const [quantities, setQuantities] = useState<Record<number, number>>({})


  useEffect(() => {
    if (!token) {
      router.push('/login')
      return
    }
    fetchCart()
  }, [token])

  useEffect(() => {
    const map: Record<number, number> = {}
    items.forEach(i => {
      map[i.product.id] = i.quantity
    })
    setQuantities(map)
  }, [items])

  if (loading) return <p className="p-4">Cargando carrito…</p>
  if (error) return <p className="p-4 text-red-600">{error}</p>
  if (!items.length) return <p className="p-4">Tu carrito está vacío.</p>

  const total = items
    .reduce((sum, i) => sum + Number(i.product.price) * i.quantity, 0)
    .toLocaleString()

  const handleQuantityChange = (productId: number, value: number) => {
    const qty = Math.max(1, value)
    setQuantities(prev => ({ ...prev, [productId]: qty }))
    updateItemQuantity(productId, qty)
  }

  return (
    <div className="p-6">
      <h1 className="text-2xl mb-4">Tu Carrito</h1>
      <ul className="space-y-4">
        {items.map(({ product, quantity }) => {
          const currentQty = quantities[product.id] ?? quantity
          const lineTotal = (Number(product.price) * currentQty).toLocaleString()
          return (
            <li key={product.id} className="flex items-center space-x-4">
              <img
                src={product.imageUrl}
                alt={product.name}
                className="h-16 w-16 object-cover rounded"
              />
              <div className="flex-1">
                <p className="font-semibold">{product.name}</p>
                <div className="flex items-center space-x-2">
                  <input
                    type="number"
                    min={1}
                    value={currentQty}
                    onChange={e =>
                      handleQuantityChange(
                        product.id,
                        parseInt(e.target.value, 10) || 1
                      )
                    }
                    className="w-16 p-1 border rounded text-center"
                  />
                </div>
              </div>
              <p className="font-bold">Gs. {lineTotal}</p>
              <button
                onClick={() => removeItem(product.id)}
                className="text-red-600 hover:underline"
              >
                Eliminar
              </button>
            </li>
          )
        })}
      </ul>

      <div className="flex justify-between items-center mt-6">
        <p className="text-xl font-bold">Total: Gs. {total}</p>
        <div className="space-x-2">
          <button
            onClick={clearCart}
            className="px-4 py-2 border rounded hover:bg-gray-100"
          >
            Vaciar carrito
          </button>
          <button
            onClick={checkout}
            className="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700"
          >
            Checkout
          </button>
        </div>
      </div>
    </div>
  )
}
