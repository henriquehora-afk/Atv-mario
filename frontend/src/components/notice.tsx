export function Notice({ children, kind = "error" }: { children: React.ReactNode; kind?: "error" | "success" }) {
  return (
    <div role="status" className={`rounded-md px-4 py-3 text-sm ${kind === "success" ? "bg-green-50 text-green-800" : "bg-red-50 text-red-700"}`}>
      {children}
    </div>
  );
}
