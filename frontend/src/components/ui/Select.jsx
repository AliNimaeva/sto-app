export default function Select({ label, children, className = "", ...props }) {
    return (
        <div className="flex flex-col gap-1">
            {label && <label className="text-sm font-medium text-gray-700">{label}</label>}
            <select
                className={`px-3 py-2 border border-gray-300 rounded-lg bg-white focus:outline-none focus:ring-2 focus:ring-blue-500 ${className}`}
                {...props}
            >
                {children}
            </select>
        </div>
    );
}