import axios from 'axios'

const BASE_URL = 'http://localhost:8080/api/categoria'

export const listarCategorias = () => axios.get(`${BASE_URL}/listar`)
export const buscarCategoria = (id) => axios.get(`${BASE_URL}/buscar/${id}`)
export const agregarCategoria = (categoria) => axios.post(`${BASE_URL}/agregar`, categoria)
export const modificarCategoria = (categoria) => axios.put(`${BASE_URL}/modificar`, categoria)
export const eliminarCategoria = (id) => axios.delete(`${BASE_URL}/eliminar/${id}`)
export const activarCategoria = (id) => axios.put(`${BASE_URL}/activar/${id}`)
export const desactivarCategoria = (id) => axios.put(`${BASE_URL}/desactivar/${id}`)