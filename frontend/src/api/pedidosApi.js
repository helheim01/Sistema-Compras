import axios from 'axios'

const BASE_URL = 'http://localhost:8080/api/pedido'

export const listarPedidos = () => axios.get(`${BASE_URL}/listar`)
export const buscarPedido = (id) => axios.get(`${BASE_URL}/buscar/${id}`)
export const agregarPedido = (pedido) => axios.post(`${BASE_URL}/agregar`, pedido)
export const modificarPedido = (pedido) => axios.put(`${BASE_URL}/modificar`, pedido)
export const eliminarPedido = (id) => axios.delete(`${BASE_URL}/eliminar/${id}`)