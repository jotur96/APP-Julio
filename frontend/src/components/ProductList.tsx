'use client'
import { useEffect, useState } from 'react'
import { useRouter } from 'next/navigation'
import { api } from '@/services/api'
import { useAuth } from '@/context/AuthContext'
import { useCart } from '@/context/CartContext'

interface Producto {
  id: number
  name: string
  description: string
  price?: number
  imageUrl: string
}

export function ProductsList() {
  const { token } = useAuth()
  const router = useRouter()
  const [items, setItems] = useState<Producto[]>([])
  const [loading, setLoading] = useState(true)
  const { addItem } = useCart()

  // Mantenemos un map de cantidades por producto
  const [quantities, setQuantities] = useState<Record<number, number>>({})

  useEffect(() => {
    api.get<Producto[]>('/products')
      .then(res => {
        setItems(res.data)
        // Inicializamos todas las cantidades a 1
        const initial = res.data.reduce((acc, p) => {
          acc[p.id] = 1
          return acc
        }, {} as Record<number, number>)
        setQuantities(initial)
      })
      .catch(console.error)
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <p>Cargando productos…</p>
  if (items.length === 0) return <p>No hay productos.</p>

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
      {items.map(p => {
        const precio = Number(p.price ?? 0).toLocaleString()
        const qty = quantities[p.id] ?? 1

        const handleQtyChange = (e: React.ChangeEvent<HTMLInputElement>) => {
          const v = parseInt(e.target.value, 10)
          setQuantities(q => ({ ...q, [p.id]: v > 0 ? v : 1 }))
        }

        const handleAdd = () => {
          if (!token) {
            router.push('/login')
            return
          }
          addItem(p.id, qty)
            .then(() => console.log(`Agregado al carrito: ${p.id} (x${qty})`))
            .catch(err => console.error('Error al agregar:', err))
        }

        return (
          <div key={p.id} className="border rounded p-4 flex flex-col">
            <img
              src={p.imageUrl}
              alt={p.name}
              className="h-40 w-full object-cover mb-2"
            />
            <h3 className="font-semibold">{p.name}</h3>
            <p className="flex-1 text-sm">{p.description}</p>
            <div className="mt-2 font-bold">Gs. {precio}</div>

            <div className="mt-4 flex items-center space-x-2">
              <input
                type="number"
                min={1}
                value={qty}
                onChange={handleQtyChange}
                className="w-16 p-1 border rounded text-center"
              />
              <button
                onClick={handleAdd}
                className="bg-blue-500 text-white py-1 px-3 rounded hover:bg-blue-600 transition"
              >
                Agregar
              </button>
            </div>
          </div>
        )
      })}
    </div>
  )
}
