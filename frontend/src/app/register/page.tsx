'use client'
import { useState } from 'react'
import { api } from '@/services/api'
import { useRouter } from 'next/navigation'
import { useAuth } from '@/context/AuthContext'

export default function RegisterPage() {
  const router = useRouter()
  const { login } = useAuth() 
  const [form, setForm] = useState({
    nombres: '', apellidos: '', email: '',
    direccion: '', fecha_nac: '',
    password: '', confirmPassword: '',
  })
  const [error, setError] = useState('')

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value })
    setError('')
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (form.password !== form.confirmPassword) {
      setError('Las contraseñas no coinciden')
      return
    }

    try {
      await api.post('/auth/register', {
        firstName: form.nombres,
        lastName:  form.apellidos,
        email:     form.email,
        password:  form.password,
        address:   form.direccion,
        birthDate: form.fecha_nac,
      })
      await login(form.email, form.password)
    } catch (err: any) {
      console.error(err)
      setError('Ocurrió un error al registrar. Intenta de nuevo.')
    }
  }

  return (
    <div className="max-w-md mx-auto p-6">
      <h1 className="text-2xl mb-4">Registro</h1>
      <form className="space-y-4" onSubmit={handleSubmit}>
        {['nombres','apellidos','email','direccion','fecha_nac'].map(f => (
          <div key={f}>
            <label className="block mb-1 capitalize">
              {f.replace('_',' ')}
            </label>
            <input
              name={f}
              type={f === 'fecha_nac' ? 'date' : 'text'}
              required
              className="w-full p-2 border rounded"
              value={(form as any)[f]}
              onChange={handleChange}
            />
          </div>
        ))}

        <div>
          <label className="block mb-1">Contraseña</label>
          <input
            name="password"
            type="password"
            required
            className="w-full p-2 border rounded"
            value={form.password}
            onChange={handleChange}
          />
        </div>

        <div>
          <label className="block mb-1">Confirmar contraseña</label>
          <input
            name="confirmPassword"
            type="password"
            required
            className="w-full p-2 border rounded"
            value={form.confirmPassword}
            onChange={handleChange}
          />
        </div>

        {error && <p className="text-red-600 text-sm">{error}</p>}

        <button
          type="submit"
          className="w-full bg-blue-600 text-white py-2 rounded disabled:opacity-50"
          disabled={!form.password || !form.confirmPassword}
        >
          Crear cuenta
        </button>
      </form>
    </div>
  )
}
