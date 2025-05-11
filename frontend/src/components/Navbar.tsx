'use client'
import Link from 'next/link'
import { useRouter } from 'next/navigation'
import { useAuth } from '@/context/AuthContext'

export function Navbar() {
  const { token, logout } = useAuth()
  const router = useRouter()

  return (
    <nav className="bg-gray-800 text-white p-4 flex justify-between items-center">
      <div className="space-x-4">
        <Link href="/" className="hover:underline">Productos</Link>
        <Link href="/cart"className="hover:underline">Carrito</Link>
        <Link href="/orders"className="hover:underline">Ordenes</Link>
      </div>
      <div className="space-x-4">
        {token ? (
          <button
            onClick={logout}
            className="bg-red-600 px-3 py-1 rounded hover:bg-red-700"
          >
            Cerrar sesión
          </button>
        ) : (
          <>
            <Link
              href="/login"
              className="px-3 py-1 border border-white rounded hover:bg-white hover:text-black transition"
            >
              Ingresar
            </Link>
            <Link
              href="/register"
              className="px-3 py-1 bg-blue-600 rounded hover:bg-blue-700 transition"
            >
              Registro
            </Link>
          </>
        )}
      </div>
    </nav>
  )
}
