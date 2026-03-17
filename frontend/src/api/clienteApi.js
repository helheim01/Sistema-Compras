import axios from 'axios'

const BASE_URL = 'http://localhost:8080/api/cliente'

export const listarClientes = () => axios.get(`${BASE_URL}/listar`)
export const buscarCliente = (id) => axios.get(`${BASE_URL}/buscar/${id}`)
export const agregarCliente = (cliente) => axios.post(`${BASE_URL}/agregar`, cliente)
export const modificarCliente = (cliente) => axios.put(`${BASE_URL}/modificar`, cliente)
export const eliminarCliente = (id) => axios.delete(`${BASE_URL}/eliminar/${id}`)