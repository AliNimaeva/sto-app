const variants = {
    success: "bg-green-50 border-green-200 text-green-800",
    error: "bg-red-50 border-red-200 text-red-800",
    info: "bg-blue-50 border-blue-200 text-blue-800",
};

export default function Alert({ variant = "info", children }) {
    if (!children) return null;
    return (
        <div className={`border rounded-lg px-4 py-3 text-sm ${variants[variant]}`}>
            {children}
        </div>
    );
}