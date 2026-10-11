import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/api";

function MyOrdersPage() {
    const [orders, setOrders] = useState([]);
    const [status, setStatus] = useState("");
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const statusLabels = {
        NEW: "Новый",
        ASSIGNED: "Назначен",
        AGREED: "Согласован",
        IN_PROGRESS: "В работе",
        COMPLETED: "Завершён",
        CLOSED: "Закрыт",
        REJECTED_BY_CLIENT: "Отклонён клиентом"
    };

    const loadOrders = async () => {
        setLoading(true);
        setError("");

        try {
            const response = await api.get("/orders/my");

            console.log("Мои заказы:", response.data);

            setOrders(response.data);
        } catch (error) {
            console.error(error);

            setError(
                error.response?.data?.message ||
                "Не удалось загрузить мои заказы"
            );
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadOrders();
    }, []);

    const filteredOrders = status
        ? orders.filter((order) => order.status === status)
        : orders;

    if (loading) {
        return <p>Загрузка заказов...</p>;
    }

    return (
        <div>
            <h1>Мои заказы</h1>

            <div className="card">
                <label>Фильтр по статусу</label>

                <select
                    value={status}
                    onChange={(event) => setStatus(event.target.value)}
                >
                    <option value="">Все заказы</option>
                    <option value="ASSIGNED">Назначен</option>
                    <option value="AGREED">Согласован</option>
                    <option value="IN_PROGRESS">В работе</option>
                    <option value="COMPLETED">Завершён</option>
                    <option value="CLOSED">Закрыт</option>
                    <option value="REJECTED_BY_CLIENT">
                        Отклонён клиентом
                    </option>
                </select>
            </div>

            {error && (
                <p className="error">
                    {error}
                </p>
            )}

            {!error && filteredOrders.length === 0 && (
                <div className="card">
                    <p>
                        {status
                            ? "Заказов с таким статусом нет."
                            : "Вам пока не назначены заказы."}
                    </p>
                </div>
            )}

            {filteredOrders.length > 0 && (
                <table>
                    <thead>
                        <tr>
                            <th>Номер</th>
                            <th>Автомобиль</th>
                            <th>Госномер</th>
                            <th>Проблема</th>
                            <th>Статус</th>
                            <th>Стоимость</th>
                            <th>Оплата</th>
                            <th></th>
                        </tr>
                    </thead>

                    <tbody>
                        {filteredOrders.map((order) => (
                            <tr key={order.id}>
                                <td>{order.number}</td>

                                <td>
                                    {order.brand} {order.model}
                                </td>

                                <td>
                                    {order.plateNumber}
                                </td>

                                <td>
                                    {order.problemDescription}
                                </td>

                                <td>
                                    <span className="status">
                                        {statusLabels[order.status] ||
                                            order.status}
                                    </span>
                                </td>

                                <td>
                                    {order.totalCost}
                                </td>

                                <td>
                                    {order.paid
                                        ? "Оплачен"
                                        : "Не оплачен"}
                                </td>

                                <td>
                                    <Link to={`/orders/${order.id}`}>
                                        <button>
                                            Открыть
                                        </button>
                                    </Link>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
        </div>
    );
}

export default MyOrdersPage;