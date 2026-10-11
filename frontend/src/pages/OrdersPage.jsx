import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/api";
import Header from "../components/Header";

function OrdersPage() {
    const [orders, setOrders] = useState([]);
    const [status, setStatus] = useState("");
    const [page, setPage] = useState(0);

    const [totalPages, setTotalPages] = useState(0);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const loadOrders = async () => {
        setLoading(true);
        setError("");

        try {
            const params = {
                page,
                size: 10,
            };

            if (status) {
                params.status = status;
            }

            const response = await api.get("/orders", { params });

            setOrders(response.data.content);
            setTotalPages(response.data.totalPages);
        } catch (error) {
            console.error(error);
            setError("Не удалось загрузить заказы");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadOrders();
    }, [page, status]);

    const handleStatusChange = (event) => {
        setStatus(event.target.value);
        setPage(0);
    };

    return (
        <>

            <main>
                <h1>Заказы</h1>

                <div>
                    <label>
                        Статус:{" "}
                        <select
                            value={status}
                            onChange={handleStatusChange}
                        >
                            <option value="">Все</option>
                            <option value="NEW">Новый</option>
                            <option value="ASSIGNED">Назначен</option>
                            <option value="AGREED">Согласован</option>
                            <option value="IN_PROGRESS">В работе</option>
                            <option value="COMPLETED">Завершён</option>
                            <option value="CLOSED">Закрыт</option>
                            <option value="REJECTED_BY_CLIENT">
                                Отклонён клиентом
                            </option>
                        </select>
                    </label>
                </div>

                {loading && <p>Загрузка...</p>}

                {error && <p>{error}</p>}

                {!loading && !error && orders.length === 0 && (
                    <p>Заказов нет</p>
                )}

                {!loading && orders.length > 0 && (
                    <table>
                        <thead>
                            <tr>
                                <th>Номер</th>
                                <th>Автомобиль</th>
                                <th>Проблема</th>
                                <th>Статус</th>
                                <th>Стоимость</th>
                                <th>Оплата</th>
                                <th>Действие</th>
                            </tr>
                        </thead>

                        <tbody>
                            {orders.map((order) => (
                                <tr key={order.id}>
                                    <td>{order.number}</td>

                                    <td>
                                        {order.car?.brand}{" "}
                                        {order.car?.model}
                                    </td>

                                    <td>
                                        {order.problemDescription}
                                    </td>

                                    <td>
                                        <span className="status">
                                            {order.status}
                                        </span>
                                    </td>

                                    <td>
                                        {order.totalCost} ₽
                                    </td>

                                    <td>
                                        {order.paid
                                            ? "Оплачено"
                                            : "Не оплачено"}
                                    </td>

                                    <td>
                                        <Link to={`/orders/${order.id}`}>
                                            Открыть
                                        </Link>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                )}

                {totalPages > 1 && (
                    <div>
                        <button
                            disabled={page === 0}
                            onClick={() => setPage(page - 1)}
                        >
                            Назад
                        </button>

                        <span>
                            {" "}
                            Страница {page + 1} из {totalPages}{" "}
                        </span>

                        <button
                            disabled={page >= totalPages - 1}
                            onClick={() => setPage(page + 1)}
                        >
                            Далее
                        </button>
                    </div>
                )}
            </main>
        </>
    );
}

export default OrdersPage;