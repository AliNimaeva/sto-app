import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import api from "../api/api";

function OrderPage() {
    const { id } = useParams();

    const [additionalWorks, setAdditionalWorks] = useState([]);
    const [additionalDescription, setAdditionalDescription] = useState("");
    const [additionalPrice, setAdditionalPrice] = useState("");
    const [additionalMessage, setAdditionalMessage] = useState("");
    const [additionalError, setAdditionalError] = useState("");

    const [masters, setMasters] = useState([]);
    const [selectedMasterId, setSelectedMasterId] = useState("");
    const [assignMessage, setAssignMessage] = useState("");

    const [order, setOrder] = useState(null);
    const [services, setServices] = useState([]);
    const [parts, setParts] = useState([]);

    const [error, setError] = useState("");
    const [loading, setLoading] = useState(true);

    const [newStatus, setNewStatus] = useState("");
    const [statusMessage, setStatusMessage] = useState("");

    const [diagnostics, setDiagnostics] = useState("");
    const [diagnosticsMessage, setDiagnosticsMessage] = useState("");

    const [itemType, setItemType] = useState("service");
    const [selectedItemId, setSelectedItemId] = useState("");
    const [quantity, setQuantity] = useState(1);
    const [itemMessage, setItemMessage] = useState("");

    const [paymentMessage, setPaymentMessage] = useState("");
    const [closeMessage, setCloseMessage] = useState("");

    const role = localStorage.getItem("role");

    const statusLabels = {
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

    const additionalWorkStatusLabels = {
        PENDING: "На согласовании",
        APPROVED: "Согласована",
        REJECTED: "Отклонена",
    };

    // --- вся логика остаётся без изменений ---
    // (payOrder, closeOrder, loadMasters, loadOrder, loadItems,
    //  loadAdditionalWorks, useEffect, createAdditionalWork,
    //  approveAdditionalWork, getAvailableStatuses, changeStatus,
    //  updateDiagnostics, addItem, updateItemQuantity, removeItem,
    //  assignMaster — копируете из старого файла как есть)

    const payOrder = async () => {
        try {
            const response = await api.put(`/orders/${id}/pay`);
            setOrder(response.data);
            setPaymentMessage("Заказ отмечен как оплаченный");
        } catch (error) {
            console.error(error);
            setPaymentMessage(
                error.response?.data?.message || "Не удалось отметить заказ как оплаченный"
            );
        }
    };

    const closeOrder = async () => {
        if (!window.confirm("Закрыть заказ?")) return;
        try {
            const response = await api.put(`/orders/${id}/close`);
            setOrder(response.data);
            setCloseMessage("Заказ успешно закрыт");
        } catch (error) {
            console.error(error);
            setCloseMessage(error.response?.data?.message || "Не удалось закрыть заказ");
        }
    };

    const loadMasters = async () => {
        try {
            const response = await api.get("/users");
            setMasters(response.data.filter((u) => u.role === "MASTER"));
        } catch (error) {
            console.error(error);
        }
    };

    const loadOrder = async () => {
        try {
            const response = await api.get(`/orders/${id}`);
            setOrder(response.data);
            setDiagnostics(response.data.diagnosticsDescription || "");
        } catch (error) {
            console.error(error);
            setError(error.response?.data?.message || "Не удалось загрузить заказ");
        } finally {
            setLoading(false);
        }
    };

    const loadItems = async () => {
        try {
            const [servicesResponse, partsResponse] = await Promise.all([
                api.get("/services"),
                api.get("/parts"),
            ]);
            setServices(servicesResponse.data);
            setParts(partsResponse.data);
        } catch (error) {
            console.error(error);
        }
    };

    const loadAdditionalWorks = async () => {
        try {
            const response = await api.get(`/orders/${id}/additional-works`);
            setAdditionalWorks(response.data);
        } catch (error) {
            console.error(error);
            setAdditionalError(
                error.response?.data?.message || "Не удалось загрузить дополнительные работы"
            );
        }
    };

    useEffect(() => {
        loadOrder();
        loadAdditionalWorks();
        if (role === "ADMIN" || role === "MASTER") loadItems();
        if (role === "ADMIN") loadMasters();
    }, [id]);

    const createAdditionalWork = async (event) => {
        event.preventDefault();
        setAdditionalMessage("");
        setAdditionalError("");
        try {
            await api.post(`/orders/${id}/additional-works`, {
                description: additionalDescription,
                price: Number(additionalPrice),
            });
            setAdditionalDescription("");
            setAdditionalPrice("");
            setAdditionalMessage("Дополнительная работа добавлена");
            await loadAdditionalWorks();
        } catch (error) {
            console.error(error);
            setAdditionalError(
                error.response?.data?.message || "Не удалось добавить дополнительную работу"
            );
        }
    };

    const approveAdditionalWork = async (workId, approved) => {
        setAdditionalMessage("");
        setAdditionalError("");
        try {
            await api.put(`/orders/additional-works/${workId}/approval`, null, {
                params: { approved },
            });
            setAdditionalMessage(
                approved ? "Дополнительная работа согласована" : "Дополнительная работа отклонена"
            );
            await loadAdditionalWorks();
            await loadOrder();
        } catch (error) {
            console.error(error);
            setAdditionalError(
                error.response?.data?.message || "Не удалось изменить статус дополнительной работы"
            );
        }
    };

    const getAvailableStatuses = () => {
        if (!order) return [];
        switch (order.status) {
            case "NEW":
                return [{ value: "ASSIGNED", label: "Назначен" }];
            case "ASSIGNED":
                return [{ value: "AGREED", label: "Согласован" }];
            case "AGREED":
                return [
                    { value: "IN_PROGRESS", label: "В работе" },
                    { value: "REJECTED_BY_CLIENT", label: "Отклонён клиентом" },
                ];
            case "IN_PROGRESS":
                return [{ value: "COMPLETED", label: "Завершён" }];
            default:
                return [];
        }
    };

    const changeStatus = async () => {
        if (!newStatus) return;
        try {
            const response = await api.put(`/orders/${id}/status`, { status: newStatus });
            setOrder(response.data);
            setStatusMessage("Статус успешно изменён");
            setNewStatus("");
        } catch (error) {
            console.error(error);
            setStatusMessage(error.response?.data?.message || "Не удалось изменить статус");
        }
    };

    const updateDiagnostics = async () => {
        try {
            const response = await api.put(`/orders/${id}/diagnostics`, {
                diagnosticsDescription: diagnostics,
            });
            setOrder(response.data);
            setDiagnosticsMessage("Диагностика сохранена");
        } catch (error) {
            console.error(error);
            setDiagnosticsMessage(
                error.response?.data?.message || "Не удалось сохранить диагностику"
            );
        }
    };

    const addItem = async () => {
        setItemMessage("");
        if (!selectedItemId) {
            setItemMessage("Выберите услугу или запчасть");
            return;
        }
        if (quantity < 1) {
            setItemMessage("Количество должно быть не меньше 1");
            return;
        }
        const request = {
            serviceId: itemType === "service" ? Number(selectedItemId) : null,
            partId: itemType === "part" ? Number(selectedItemId) : null,
            quantity: Number(quantity),
        };
        try {
            const response = await api.post(`/orders/${id}/items`, request);
            setOrder(response.data);
            setSelectedItemId("");
            setQuantity(1);
            setItemMessage("Позиция добавлена в заказ");
            if (itemType === "part") await loadItems();
        } catch (error) {
            console.error(error);
            setItemMessage(error.response?.data?.message || "Не удалось добавить позицию");
        }
    };

    const updateItemQuantity = async (itemId, newQuantity) => {
        if (newQuantity < 1) return;
        try {
            const response = await api.put(`/orders/${id}/items/${itemId}`, {
                quantity: newQuantity,
            });
            setOrder(response.data);
            setItemMessage("Количество обновлено");
            if (parts.length > 0) await loadItems();
        } catch (error) {
            console.error(error);
            setItemMessage(error.response?.data?.message || "Не удалось изменить количество");
        }
    };

    const removeItem = async (itemId) => {
        if (!window.confirm("Удалить эту позицию из заказа?")) return;
        try {
            const response = await api.delete(`/orders/${id}/items/${itemId}`);
            setOrder(response.data);
            setItemMessage("Позиция удалена");
            if (parts.length > 0) await loadItems();
        } catch (error) {
            console.error(error);
            setItemMessage(error.response?.data?.message || "Не удалось удалить позицию");
        }
    };

    const assignMaster = async () => {
        if (!selectedMasterId) {
            setAssignMessage("Выберите мастера");
            return;
        }
        try {
            const response = await api.put(`/orders/${id}/assign`, {
                masterId: Number(selectedMasterId),
            });
            setOrder(response.data);
            setSelectedMasterId("");
            setAssignMessage("Мастер успешно назначен");
        } catch (error) {
            console.error(error);
            setAssignMessage(error.response?.data?.message || "Не удалось назначить мастера");
        }
    };

    if (loading) {
        return (
            <main>
                <p className="text-muted">Загрузка...</p>
            </main>
        );
    }

    if (error) {
        return (
            <main>
                <p className="error">{error}</p>
                <Link to="/orders">← К заказам</Link>
            </main>
        );
    }

    if (!order) {
        return (
            <main>
                <p className="text-muted">Заказ не найден</p>
            </main>
        );
    }

    const availableStatuses = getAvailableStatuses();
    const canEditItems =
        (role === "ADMIN" || role === "MASTER") && order.status === "IN_PROGRESS";
    const canEditDiagnostics =
        (role === "ADMIN" || role === "MASTER") &&
        order.status !== "CLOSED" &&
        order.status !== "REJECTED_BY_CLIENT";

    return (
        <main>
            <div className="order-header">
                <h1>Заказ {order.number}</h1>
                <span className={`status status-${order.status}`}>
                    {statusLabels[order.status] || order.status}
                </span>
            </div>

            <div className="two-columns">
                <section className="card">
                    <h2>Основная информация</h2>
                    <dl className="kv">
                        <dt>Дата создания</dt>
                        <dd>{order.createdAt}</dd>

                        <dt>Стоимость</dt>
                        <dd>{order.totalCost} ₽</dd>

                        <dt>Оплата</dt>
                        <dd className={order.paid ? "success" : "error"}>
                            {order.paid ? "Оплачено" : "Не оплачено"}
                        </dd>

                        <dt>Мастер</dt>
                        <dd>{order.masterName || "не назначен"}</dd>
                    </dl>
                </section>

                <section className="card">
                    <h2>Автомобиль и клиент</h2>
                    <dl className="kv">
                        <dt>Марка</dt>
                        <dd>{order.brand || "—"}</dd>

                        <dt>Модель</dt>
                        <dd>{order.model || "—"}</dd>

                        <dt>Год</dt>
                        <dd>{order.year || "—"}</dd>

                        <dt>Госномер</dt>
                        <dd>{order.plateNumber || "—"}</dd>

                        <dt>Клиент</dt>
                        <dd>{order.clientName || "—"}</dd>

                        <dt>Телефон</dt>
                        <dd>{order.clientPhone || "—"}</dd>
                    </dl>
                </section>
            </div>

            <section className="card">
                <h2>Проблема</h2>
                <p>{order.problemDescription || "—"}</p>
            </section>

            <section className="card">
                <h2>Диагностика</h2>
                {canEditDiagnostics ? (
                    <div>
                        <textarea
                            rows="5"
                            value={diagnostics}
                            onChange={(e) => setDiagnostics(e.target.value)}
                        />
                        <button onClick={updateDiagnostics}>Сохранить диагностику</button>
                        {diagnosticsMessage && <p className="success">{diagnosticsMessage}</p>}
                    </div>
                ) : (
                    <p>{order.diagnosticsDescription || "—"}</p>
                )}
            </section>

            {role === "ADMIN" &&
                order.status !== "CLOSED" &&
                order.status !== "REJECTED_BY_CLIENT" && (
                    <section className="card">
                        <h2>Назначение мастера</h2>
                        <div>
                            <label>Мастер</label>
                            <select
                                value={selectedMasterId}
                                onChange={(e) => setSelectedMasterId(e.target.value)}
                            >
                                <option value="">Выберите мастера</option>
                                {masters.map((m) => (
                                    <option key={m.id} value={m.id}>
                                        {m.name} — {m.email}
                                    </option>
                                ))}
                            </select>
                            <button onClick={assignMaster} disabled={!selectedMasterId}>
                                Назначить
                            </button>
                            {assignMessage && (
                                <p className={assignMessage.includes("успешно") ? "success" : "error"}>
                                    {assignMessage}
                                </p>
                            )}
                        </div>
                    </section>
                )}

            {availableStatuses.length > 0 && (role === "ADMIN" || role === "MASTER") && (
                <section className="card">
                    <h2>Изменить статус</h2>
                    <div>
                        <label>Новый статус</label>
                        <select
                            value={newStatus}
                            onChange={(e) => setNewStatus(e.target.value)}
                        >
                            <option value="">Выберите статус</option>
                            {availableStatuses.map((s) => (
                                <option key={s.value} value={s.value}>
                                    {s.label}
                                </option>
                            ))}
                        </select>
                        <button onClick={changeStatus} disabled={!newStatus}>
                            Изменить
                        </button>
                        {statusMessage && (
                            <p className={statusMessage.includes("успешно") ? "success" : "error"}>
                                {statusMessage}
                            </p>
                        )}
                    </div>
                </section>
            )}

            <section className="card">
                <h2>Услуги и запчасти</h2>

                {order.items && order.items.length > 0 ? (
                    <table>
                        <thead>
                            <tr>
                                <th>Тип</th>
                                <th>Название</th>
                                <th>Кол-во</th>
                                <th>Цена</th>
                                {canEditItems && <th>Действия</th>}
                            </tr>
                        </thead>
                        <tbody>
                            {order.items.map((item) => (
                                <tr key={item.id}>
                                    <td>{item.serviceId ? "Услуга" : "Запчасть"}</td>
                                    <td>{item.serviceName || item.partName || "—"}</td>
                                    <td>
                                        {canEditItems ? (
                                            <div className="qty-controls">
                                                <button
                                                    onClick={() =>
                                                        updateItemQuantity(item.id, item.quantity - 1)
                                                    }
                                                    disabled={item.quantity <= 1}
                                                >
                                                    −
                                                </button>
                                                <span>{item.quantity}</span>
                                                <button
                                                    onClick={() =>
                                                        updateItemQuantity(item.id, item.quantity + 1)
                                                    }
                                                >
                                                    +
                                                </button>
                                            </div>
                                        ) : (
                                            item.quantity
                                        )}
                                    </td>
                                    <td>{item.price} ₽</td>
                                    {canEditItems && (
                                        <td>
                                            <button
                                                className="danger"
                                                onClick={() => removeItem(item.id)}
                                            >
                                                Удалить
                                            </button>
                                        </td>
                                    )}
                                </tr>
                            ))}
                        </tbody>
                    </table>
                ) : (
                    <p className="text-muted">В заказе пока нет услуг или запчастей.</p>
                )}

                {canEditItems && (
                    <div style={{ marginTop: 20 }}>
                        <h3>Добавить позицию</h3>
                        <div>
                            <label>Тип</label>
                            <select
                                value={itemType}
                                onChange={(e) => {
                                    setItemType(e.target.value);
                                    setSelectedItemId("");
                                }}
                            >
                                <option value="service">Услуга</option>
                                <option value="part">Запчасть</option>
                            </select>
                        </div>
                        <div>
                            <label>{itemType === "service" ? "Услуга" : "Запчасть"}</label>
                            <select
                                value={selectedItemId}
                                onChange={(e) => setSelectedItemId(e.target.value)}
                            >
                                <option value="">Выберите позицию</option>
                                {itemType === "service" &&
                                    services.map((s) => (
                                        <option key={s.id} value={s.id}>
                                            {s.name} — {s.price} ₽
                                        </option>
                                    ))}
                                {itemType === "part" &&
                                    parts.map((p) => (
                                        <option
                                            key={p.id}
                                            value={p.id}
                                            disabled={p.quantity === 0}
                                        >
                                            {p.name} — {p.price} ₽ — остаток: {p.quantity}
                                        </option>
                                    ))}
                            </select>
                        </div>
                        <div>
                            <label>Количество</label>
                            <input
                                type="number"
                                min="1"
                                value={quantity}
                                onChange={(e) => setQuantity(e.target.value)}
                            />
                        </div>
                        <button onClick={addItem}>Добавить</button>
                        {itemMessage && (
                            <p
                                className={
                                    itemMessage.includes("добавлена") ||
                                    itemMessage.includes("обновлено") ||
                                    itemMessage.includes("удалена")
                                        ? "success"
                                        : "error"
                                }
                            >
                                {itemMessage}
                            </p>
                        )}
                    </div>
                )}
            </section>

            <section className="card">
                <h2>Дополнительные работы</h2>

                {additionalError && <p className="error">{additionalError}</p>}
                {additionalMessage && <p className="success">{additionalMessage}</p>}

                {role === "MASTER" && order.status === "IN_PROGRESS" && (
                    <form onSubmit={createAdditionalWork}>
                        <h3>Добавить дополнительную работу</h3>
                        <div>
                            <label>Описание</label>
                            <textarea
                                value={additionalDescription}
                                onChange={(e) => setAdditionalDescription(e.target.value)}
                                required
                            />
                        </div>
                        <div>
                            <label>Стоимость</label>
                            <input
                                type="number"
                                min="0.01"
                                step="0.01"
                                value={additionalPrice}
                                onChange={(e) => setAdditionalPrice(e.target.value)}
                                required
                            />
                        </div>
                        <button type="submit">Добавить работу</button>
                    </form>
                )}

                {additionalWorks.length === 0 ? (
                    <p className="text-muted">Дополнительных работ пока нет.</p>
                ) : (
                    <table>
                        <thead>
                            <tr>
                                <th>Описание</th>
                                <th>Стоимость</th>
                                <th>Статус</th>
                                <th>Создал</th>
                                <th>Дата</th>
                                {role === "ADMIN" && <th>Действия</th>}
                            </tr>
                        </thead>
                        <tbody>
                            {additionalWorks.map((work) => (
                                <tr key={work.id}>
                                    <td>{work.description}</td>
                                    <td>{Number(work.price).toFixed(2)} ₽</td>
                                    <td>
                                        <span className={`status status-${work.status}`}>
                                            {additionalWorkStatusLabels[work.status] ||
                                                work.status}
                                        </span>
                                    </td>
                                    <td>{work.createdByName || "-"}</td>
                                    <td>
                                        {work.createdAt
                                            ? new Date(work.createdAt).toLocaleString("ru-RU")
                                            : "-"}
                                    </td>
                                    {role === "ADMIN" && (
                                        <td>
                                            {work.status === "PENDING" && (
                                                <>
                                                    <button
                                                        className="success"
                                                        onClick={() =>
                                                            approveAdditionalWork(work.id, true)
                                                        }
                                                    >
                                                        Согласовать
                                                    </button>
                                                    <button
                                                        className="danger"
                                                        onClick={() =>
                                                            approveAdditionalWork(work.id, false)
                                                        }
                                                    >
                                                        Отклонить
                                                    </button>
                                                </>
                                            )}
                                        </td>
                                    )}
                                </tr>
                            ))}
                        </tbody>
                    </table>
                )}
            </section>

            {(role === "ADMIN" || role === "MANAGER") &&
                order.status === "COMPLETED" &&
                !order.paid && (
                    <section className="card">
                        <h2>Оплата</h2>
                        <p>Заказ завершён, но ещё не оплачен.</p>
                        <button onClick={payOrder}>Отметить как оплаченный</button>
                        {paymentMessage && <p className="success">{paymentMessage}</p>}
                    </section>
                )}

            {(role === "ADMIN" || role === "MANAGER") &&
                order.status === "COMPLETED" &&
                order.paid && (
                    <section className="card">
                        <h2>Закрытие заказа</h2>
                        <p>Заказ оплачен и готов к закрытию.</p>
                        <button className="success" onClick={closeOrder}>
                            Закрыть заказ
                        </button>
                        {closeMessage && <p className="success">{closeMessage}</p>}
                    </section>
                )}
        </main>
    );
}

export default OrderPage;