import { useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/api";

function CheckOrderPage() {
    const [number, setNumber] = useState("");
    const [order, setOrder] = useState(null);
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const statusLabels = {
        NEW: "Новая заявка",
        ASSIGNED: "Назначен мастер",
        AGREED: "Ожидает согласования",
        IN_PROGRESS: "В ремонте",
        COMPLETED: "Ремонт завершён",
        CLOSED: "Заказ закрыт",
        REJECTED_BY_CLIENT: "Отказ клиента",
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        setError("");
        setOrder(null);
        setLoading(true);

        try {
            const response = await api.get(
                `/orders/number/${number.trim()}`
            );

            setOrder(response.data);
        } catch (error) {
            console.error(error);

            if (error.response?.status === 404) {
                setError("Заказ с таким номером не найден");
            } else {
                setError("Не удалось получить информацию о заказе");
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <div>
            <h1>Проверка заказа</h1>

            <p>
                Введите номер заказа, который вы получили
                после создания заявки.
            </p>

            <form onSubmit={handleSubmit}>
                <div>
                    <label>Номер заказа</label>

                    <input
                        type="text"
                        value={number}
                        onChange={(event) => setNumber(event.target.value)}
                        placeholder="Например: STO-D59B939C"
                        required
                    />
                </div>

                {error && (
                    <p className="error">
                        {error}
                    </p>
                )}

                <button type="submit" disabled={loading}>
                    {loading ? "Поиск..." : "Получить информацию"}
                </button>
            </form>

            {order && (
                <div className="card">
                    <h2>
                        Заказ №{order.number}
                    </h2>

                    <p>
                        <strong>Статус:</strong>{" "}
                        <span className="status">
                            {statusLabels[order.status] || order.status}
                        </span>
                    </p>

                    <h3>Клиент</h3>

                    <p>
                        <strong>Имя:</strong>{" "}
                        {order.clientName || "Не указано"}
                    </p>

                    <p>
                        <strong>Телефон:</strong>{" "}
                        {order.clientPhone || "Не указан"}
                    </p>

                    <h3>Автомобиль</h3>

                    <p>
                        <strong>Марка:</strong>{" "}
                        {order.brand || "Не указана"}
                    </p>

                    <p>
                        <strong>Модель:</strong>{" "}
                        {order.model || "Не указана"}
                    </p>

                    <p>
                        <strong>Год выпуска:</strong>{" "}
                        {order.year || "Не указан"}
                    </p>

                    <p>
                        <strong>Государственный номер:</strong>{" "}
                        {order.plateNumber || "Не указан"}
                    </p>

                    <h3>Описание проблемы</h3>

                    <p>
                        {order.problemDescription || "Не указано"}
                    </p>

                    {order.diagnosticsDescription && (
                        <>
                            <h3>Диагностика</h3>

                            <p>
                                {order.diagnosticsDescription}
                            </p>
                        </>
                    )}

                    {order.masterName && (
                        <>
                            <h3>Мастер</h3>

                            <p>
                                {order.masterName}
                            </p>
                        </>
                    )}

                    <h3>Стоимость</h3>

                    <p>
                        <strong>
                            {order.totalCost ?? 0} ₽
                        </strong>
                    </p>

                    {order.items && order.items.length > 0 && (
                        <>
                            <h3>Работы и запчасти</h3>

                            <table>
                                <thead>
                                    <tr>
                                        <th>Наименование</th>
                                        <th>Количество</th>
                                        <th>Цена</th>
                                    </tr>
                                </thead>

                                <tbody>
                                    {order.items.map((item) => (
                                        <tr key={item.id}>
                                            <td>
                                                {item.serviceName ||
                                                    item.partName ||
                                                    "Позиция"}
                                            </td>

                                            <td>
                                                {item.quantity}
                                            </td>

                                            <td>
                                                {item.price} ₽
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </>
                    )}

                    <p>
                        <strong>
                            {order.paid
                                ? "Заказ оплачен"
                                : "Заказ не оплачен"}
                        </strong>
                    </p>
                </div>
            )}

            <p>
                <Link to="/">
                    Вернуться на главную
                </Link>
            </p>
        </div>
    );
}

export default CheckOrderPage;