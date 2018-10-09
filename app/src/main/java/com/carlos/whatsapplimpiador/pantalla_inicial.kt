package com.carlos.whatsapplimpiador


import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.adapter_layout.view.*
import kotlinx.android.synthetic.main.fragment_pantalla_inicial.*
import java.io.File


class pantalla_inicial : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pantalla_inicial, container, false)
    }

    override fun onStart() {
        super.onStart()

        pantalla_i_recycler.layoutManager = LinearLayoutManager(activity!!.applicationContext)
        pantalla_i_recycler.adapter = Adaptador((activity as actividad_principal).lista_archivos_total)

    }

    class Adaptador(val lista: MutableList<lista_principal>) : RecyclerView.Adapter<ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_layout, parent, false))
        }

        override fun getItemCount(): Int {
            return lista.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val datos = lista.get(position)

            holder.view.pantalla_i_titulo.text = datos.nombre
            if (position == 0) {
                holder.view.pantalla_i_cantidad.text = "${datos.cantidad} Video(s)"
            } else {
                holder.view.pantalla_i_cantidad.text = "${datos.cantidad} Imagenes"
            }
            holder.view.pantalla_i_espacio.text = "${datos.tamaño} GB"

            holder.posicion = position
            holder.datos = datos

        }

    }

    class ViewHolder(val view: View, var posicion: Int? = null, var datos: lista_principal? = null) : RecyclerView.ViewHolder(view) {
        init {
            view.setOnLongClickListener(object : View.OnLongClickListener {
                override fun onLongClick(v: View?): Boolean {

                    if (posicion == 0) {

                        if ((view.context as actividad_principal).lista_videos.size > 0) {

                            AlertDialog.Builder(v!!.context)
                                    .setIcon(R.drawable.ic_delete)
                                    .setTitle(datos!!.nombre)
                                    .setMessage("¿Esta seguro que desea borrar ${datos!!.cantidad} ${datos!!.nombre.split(" ")[1].toLowerCase()}, definitivamente?")
                                    .setPositiveButton("SI") { v, _ ->
                                        borrar_archivos(posicion!!, view)
                                    }
                                    .setNegativeButton("NO") { _, _ -> }
                                    .show()
                        } else {
                            Toast.makeText(view.context, "No hay archivos de videos para borrar", Toast.LENGTH_LONG).show()
                        }
                    }


                    if (posicion == 1) {

                        if ((view.context as actividad_principal).lista_imagenes.size > 0) {

                            AlertDialog.Builder(v!!.context)
                                    .setIcon(R.drawable.ic_delete)
                                    .setTitle(datos!!.nombre)
                                    .setMessage("¿Esta seguro que desea borrar ${datos!!.cantidad} ${datos!!.nombre.split(" ")[1].toLowerCase()}, definitivamente?")
                                    .setPositiveButton("SI") { v, _ ->
                                        borrar_archivos(posicion!!, view)
                                    }
                                    .setNegativeButton("NO") { _, _ -> }
                                    .show()
                        } else {
                            Toast.makeText(view.context, "No hay archivos de imagenes para borrar", Toast.LENGTH_LONG).show()
                        }
                    }


                    return false
                }

            })
        }

        fun borrar_archivos(posicion: Int, view: View) {
            var progreso = ProgressDialog(view.context)
            progreso.setMessage("Borrando Archivo(s)")
            progreso.show()

            if (posicion == 0) {

                (view.context as actividad_principal).lista_videos.forEach {
                    File(it.path).delete()
                }


                (view.context as actividad_principal).supportFragmentManager.popBackStack()
                (view.context as actividad_principal).programa()
                Toast.makeText(view.context, "Se han borrado de manera correcta los archivos", Toast.LENGTH_LONG)
            }


            if (posicion == 1) {

                (view.context as actividad_principal).lista_imagenes.forEach {
                    File(it.path).delete()
                }


                (view.context as actividad_principal).supportFragmentManager.popBackStack()
                (view.context as actividad_principal).programa()

                Toast.makeText(view.context, "Se han borrado de manera correcta los archivos", Toast.LENGTH_LONG)
            }

            progreso.dismiss()
        }


    }

}

