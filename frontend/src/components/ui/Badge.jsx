const colors = {
    NEW: "bg-blue-100 text-blue-800",
    ASSIGNED: "bg-yellow-100 text-yellow-800",
    AGREED: "bg-indigo-100 text-indigo-800",
    IN_PROGRESS: "bg-orange-100 text-orange-800",
    COMPLETED: "bg-green-100 text-green-800",
    CLOSED: "bg-gray-100 text-gray-800",
    REJECTED_BY_CLIENT: "bg-red-100 text-red-800",
    PENDING: "bg-yellow-100 text-yellow-800",
    APPROVED: "bg-green-100 text-green-800",
    REJECTED: "bg-red-100 text-red-800",
};

const labels = {
    NEW: "Новая",
    ASSIGNED: "Назначена",
    AGREED: "Согласована",
    IN_PROGRESS: "В работе",
    COMPLETED: "Завершена",
    CLOSED: "Закрыта",
    REJECTED_BY_CLIENT: "Отклонена клиентом",
    PENDING: "На согласовании",
    APPROVED: "Согласована",
    REJECTED: "Отклонена",
};

export default function Badge({ status }) {
    return (
        <span className={`px-2.5 py-1 rounded-full text-xs font-medium ${colors[status] || "bg-gray-100 text-gray-800"}`}>
            {labels[status] || status}
        </span>
    );
}