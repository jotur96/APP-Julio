'use client'
import { createContext, ReactNode, useContext, useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import { api } from '@/services/api'
import { useAuth } from '@/context/AuthContext'

export interface Producto {
  id: number
  name: string
  description: string
  price: number
  imageUrl: string
}

export interface CartItem {
  productId: number
  quantity: number
  product: Producto
}

interface CartContextType {
  items: CartItem[]
  loading: boolean
  error: string
  fetchCart: () => Promise<void>
  addItem: (productId: number, quantity: number) => Promise<void>
  removeItem: (productId: number) => Promise<void>
  clearCart: () => Promise<void>
  checkout: () => Promise<void>
  updateItemQuantity: (productId: number, quantity: number) => Promise<void>
}

const CartContext = createContext<CartContextType>({} as CartContextType)

export function CartProvider({ children }: { children: ReactNode }) {
  const { token } = useAuth()
  const router = useRouter()
  const [items, setItems] = useState<CartItem[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const fetchCart = async () => {
    if (!token) return
    setLoading(true)
    try {
      // Llamada real a tu API
      const res = await api.get<{ product: Producto; quantity: number }[]>('/cart/items')
      // Normalizamos cada item para incluir productId
      const mapped: CartItem[] = res.data.map(item => ({
        productId: item.product.id,
        quantity: item.quantity,
        product: item.product,
      }))
      setItems(mapped)
    } catch (err: any) {
      setError(err.message || 'Error al cargar carrito')
    } finally {
      setLoading(false)
    }
  }

  const addItem = async (productId: number, quantity: number) => {
    if (!token) {
      router.push('/login')
      return
    }
    setLoading(true)
    try {
      await api.post('/cart/items', { productId, quantity })
      await fetchCart()
    } catch (err: any) {
      setError(err.message || 'No se pudo agregar el ítem')
    } finally {
      setLoading(false)
    }
  }

  const updateItemQuantity = async (productId: number, quantity: number) => {
    if (!token) {
      router.push('/login')
      return
    }
    setLoading(true)
    try {
      await api.post(`/cart/items`, {productId, quantity })
      setItems(prev =>
        prev.map(i =>
          i.productId === productId ? { ...i, quantity } : i
        )
      )
    } catch (err: any) {
      setError(err.message || 'No se pudo actualizar la cantidad')
    } finally {
      setLoading(false)
    }
  }

  const removeItem = async (productId: number) => {
    if (!token) {
      router.push('/login')
      return
    }
    setLoading(true)
    try {
      await api.delete(`/cart/items/${productId}`)
      // Filtramos usando productId (que acabamos de normalizar)
      setItems(prev => prev.filter(i => i.productId !== productId))
    } catch (err: any) {
      setError(err.message || 'No se pudo eliminar el ítem')
    } finally {
      setLoading(false)
    }
  }

  const clearCart = async () => {
    if (!token) {
      router.push('/login')
      return
    }
    setLoading(true)
    try {
      await api.delete('/cart/items')
      setItems([])
    } catch (err: any) {
      setError(err.message || 'No se pudo vaciar el carrito')
    } finally {
      setLoading(false)
    }
  }

  const checkout = async () => {
    if (!token) {
      router.push('/login')
      return
    }
    setLoading(true)
    try {
      const res = await api.post<{ orderId: number }>('/cart/orders/checkout')
      setItems([])
      router.push(`/orders/${res.data.orderId}`)
    } catch (err: any) {
      setError(err.message || 'Error al realizar checkout')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    if (token) fetchCart()
    else setItems([])
  }, [token])

  return (
    <CartContext.Provider value={{
      items, loading, error,
      fetchCart, addItem, removeItem, clearCart, checkout, updateItemQuantity
    }}>
      {children}
    </CartContext.Provider>
  )
}

export const useCart = () => useContext(CartContext)
