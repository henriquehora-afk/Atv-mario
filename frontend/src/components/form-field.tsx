"use client";

type Props = {
  label: string; name: string; type?: string; value: string;
  onChange: (value: string) => void; placeholder?: string; autoComplete?: string;
};
export function FormField({ label, name, type = "text", value, onChange, placeholder, autoComplete }: Props) {
  return (
    <label className="block">
      <span className="mb-2 block text-sm font-medium text-slate-700">{label}</span>
      <input
        className="w-full rounded-md border border-slate-300 bg-white px-4 py-3 text-sm outline-none transition placeholder:text-slate-400 focus:border-emerald-600 focus:ring-4 focus:ring-emerald-100"
        name={name} type={type} value={value} onChange={(event) => onChange(event.target.value)}
        placeholder={placeholder} autoComplete={autoComplete} required
      />
    </label>
  );
}
