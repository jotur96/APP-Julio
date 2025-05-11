import Image from 'next/image'
import { ProductsList } from '@/components/ProductList'

export default function Home() {
  return (
    <div className="grid grid-rows-[20px_1fr_20px] items-center justify-items-center min-h-screen p-8 pb-20 gap-16 sm:p-20 font-[family-name:var(--font-geist-sans)]">
      <main className="flex flex-col gap-[32px] row-start-2 items-center sm:items-start">
        {/* Tu encabezado actual */}
        <Image
          className="dark:invert"
          src="/next.svg"
          alt="Next.js logo"
          width={180}
          height={38}
          priority
        />
        <section className="w-full mt-8">
          <h2 className="text-2xl mb-4">Nuestros Productos</h2>
          <ProductsList />
        </section>
      </main>

      <footer className="row-start-3 flex gap-[24px] flex-wrap items-center justify-center">
        {/* tu footer actual */}
      </footer>
    </div>
  )
}
