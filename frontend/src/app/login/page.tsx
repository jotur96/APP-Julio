"use client";
import { useState } from "react";
import { useAuth } from "@/context/AuthContext";

export default function LoginPage() {
  const { login } = useAuth();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const submit = (e: React.FormEvent) => {
    e.preventDefault();
    login(email, password);
  };

  return (
    <div className="max-w-sm mx-auto p-6">
      <h1 className="text-2xl mb-4">Iniciar sesión</h1>
      <form className="space-y-4" onSubmit={submit}>
        <div>
          <label>Email</label>
          <input
            type="email"
            required
            className="w-full p-2 border rounded"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
        </div>
        <div>
          <label>Contraseña</label>
          <input
            type="password"
            required
            className="w-full p-2 border rounded"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </div>
        <button className="w-full bg-green-600 text-white py-2 rounded">
          Entrar
        </button>
      </form>
      <p className="mt-4">
        ¿No tienes cuenta?{" "}
        <a href="/register" className="text-blue-600 hover:underline">
          Regístrate
        </a>
      </p>
    </div>
  );
}
