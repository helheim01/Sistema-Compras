import axios from 'axios'

const BASE_URL = 'http://localhost:8080/api/admin'

export const listarAdmins = () => axios.get(`${BASE_URL}/listar`)
export const buscarAdmin = (id) => axios.get(`${BASE_URL}/buscar/${id}`)
export const agregarAdmin = (admin) => axios.post(`${BASE_URL}/agregar`, admin)
export const modificarAdmin = (admin) => axios.put(`${BASE_URL}/modificar`, admin)
export const eliminarAdmin = (id) => axios.delete(`${BASE_URL}/eliminar/${id}`)