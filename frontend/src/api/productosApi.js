import axios from 'axios'

const BASE_URL = 'http://localhost:8080/api/producto'

export const listarProductos = () => axios.get(`${BASE_URL}/listar`)
export const buscarProducto = (id) => axios.get(`${BASE_URL}/buscar/${id}`)
export const agregarProducto = (producto) => axios.post(`${BASE_URL}/agregar`, producto)
export const modificarProducto = (producto) => axios.put(`${BASE_URL}/modificar`, producto)
export const eliminarProducto = (id) => axios.delete(`${BASE_URL}/eliminar/${id}`)
export const activarProducto = (id) => axios.put(`${BASE_URL}/activar/${id}`)
export const desactivarProducto = (id) => axios.put(`${BASE_URL}/desactivar/${id}`)