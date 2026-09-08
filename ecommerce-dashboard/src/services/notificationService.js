import notificationApi from "../api/notificationAxios";
import axios from "axios";


// GET /api/notifications -> Notification[]
const getAll = () => notificationApi.get("/api/notifications");

// GET /api/notifications/customer/{customerId} -> Notification[]
const getByCustomer = (customerId) =>
    notificationApi.get(`/api/notifications/customer/${customerId}`);


// GET /api/notifications/order/{orderId} -> Notification[]
const getByOrder = (orderId) =>
    notificationApi.get(`/api/notifications/order/${orderId}`);


const notificationService = { getAll, getByCustomer, getByOrder };

export default notificationService;
