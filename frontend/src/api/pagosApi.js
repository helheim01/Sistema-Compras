import axios from 'axios'

const BASE_URL = 'http://localhost:8080/pago'

export const listarPagos = () => axios.get(`${BASE_URL}/listar`)
export const buscarPago = (id) => axios.get(`${BASE_URL}/buscar/${id}`)
export const agregarPago = (pago) => axios.post(`${BASE_URL}/agregar`, pago)
export const modificarPago = (pago) => axios.put(`${BASE_URL}/modificar`, pago)
export const eliminarPago = (id) => axios.delete(`${BASE_URL}/eliminar/${id}`)