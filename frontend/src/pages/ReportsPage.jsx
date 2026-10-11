import { useState } from "react";
import api from "../api/api";

function ReportsPage() {
    const [from, setFrom] = useState("");
    const [to, setTo] = useState("");

    const [orderReport, setOrderReport] = useState(null);
    const [masterReport, setMasterReport] = useState([]);

    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const loadReports = async (event) => {
        event.preventDefault();

        if (!from || !to) {
            setError("Укажите начало и конец периода");
            return;
        }

        if (from > to) {
            setError("Дата начала не может быть позже даты окончания");
            return;
        }

        setError("");
        setLoading(true);

        try {
            const [ordersResponse, mastersResponse] = await Promise.all([
                api.get("/reports/orders", {
                    params: {
                        from,
                        to
                    }
                }),
                api.get("/reports/masters", {
                    params: {
                        from,
                        to
                    }
                })
            ]);

            setOrderReport(ordersResponse.data);
            setMasterReport(mastersResponse.data);
        } catch (error) {
            console.error(error);

            setError(
                error.response?.data?.message ||
                "Не удалось загрузить отчёты"
            );
        } finally {
            setLoading(false);
        }
    };

    return (
        <div>
            <h1>Отчёты</h1>

            <form onSubmit={loadReports}>
                <div>
                    <label>Дата начала</label><br />
                    <input
                        type="date"
                        value={from}
                        onChange={(event) => setFrom(event.target.value)}
                        required
                    />
                </div>

                <div>
                    <label>Дата окончания</label><br />
                    <input
                        type="date"
                        value={to}
                        onChange={(event) => setTo(event.target.value)}
                        required
                    />
                </div>

                <button type="submit" disabled={loading}>
                    {loading ? "Загрузка..." : "Сформировать отчёт"}
                </button>
            </form>

            {error && <p>{error}</p>}

            {orderReport && (
                <div>
                    <h2>Отчёт по заказам</h2>

                    <p>
                        <strong>Всего заказов:</strong>{" "}
                        {orderReport.totalOrders}
                    </p>

                    <p>
                        <strong>Оплачено заказов:</strong>{" "}
                        {orderReport.paidOrders}
                    </p>

                    <p>
                        <strong>Выручка:</strong>{" "}
                        {orderReport.paidRevenue}
                    </p>

                    <h3>Заказы по статусам</h3>

                    <table border="1">
                        <thead>
                            <tr>
                                <th>Статус</th>
                                <th>Количество</th>
                            </tr>
                        </thead>

                        <tbody>
                            {orderReport.ordersByStatus &&
                                Object.entries(orderReport.ordersByStatus).map(
                                    ([status, count]) => (
                                        <tr key={status}>
                                            <td>{status}</td>
                                            <td>{count}</td>
                                        </tr>
                                    )
                                )}
                        </tbody>
                    </table>
                </div>
            )}

            {orderReport && (
                <div>
                    <h2>Отчёт по мастерам</h2>

                    <table border="1">
                        <thead>
                            <tr>
                                <th>Мастер</th>
                                <th>Всего заказов</th>
                                <th>Завершено</th>
                                <th>Выручка по оплатам</th>
                            </tr>
                        </thead>

                        <tbody>
                            {masterReport.map((master) => (
                                <tr key={master.masterId}>
                                    <td>{master.masterName}</td>
                                    <td>{master.totalOrders}</td>
                                    <td>{master.completedOrders}</td>
                                    <td>{master.paidRevenue}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
}

export default ReportsPage;