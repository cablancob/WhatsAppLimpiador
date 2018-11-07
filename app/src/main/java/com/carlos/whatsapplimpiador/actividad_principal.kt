package com.carlos.whatsapplimpiador

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment.getExternalStorageDirectory
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import java.io.File
import java.util.*


class actividad_principal : AppCompatActivity() {

    private var tamaño = "0.00"
    private var cantidad = 0
    var lista_videos: MutableList<archivo> = mutableListOf()
    var lista_imagenes: MutableList<archivo> = mutableListOf()
    var lista_archivos_total: MutableList<lista_principal> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actividad_principal)

        validar_permisos()

    }

    fun programa() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 0)

        val am = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        var intent = Intent(this, limpiador_automatico::class.java)
        intent.action = "com.carlos.limpiadorautomatico"
        val pi = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pi)

        //Videos

        lista_archivos_total.clear()
        var file = File(getExternalStorageDirectory().absolutePath, "WhatsApp/Media/WhatsApp Video")


        lista_videos.clear()
        file.listFiles().forEach {
            if (it.isFile) {
                lista_videos.add(archivo(File(it.path).name, (File(it.path).lastModified() / 1000).toString(), it.path, File(it.path).length().toDouble()))
            }
        }

        if (lista_videos.size == 0) {
            tamaño = "0,00"
            cantidad = 0
        } else {
            tamaño = String.format("%.2f", lista_videos.sumByDouble { it.tamaño } / (1024 * 1024 * 1024))
            cantidad = lista_videos.size
        }


        lista_archivos_total.add(lista_principal("Whatsapp Videos", cantidad.toString(), tamaño))

        //Imagenes

        lista_imagenes.clear()

        file = File(getExternalStorageDirectory().absolutePath, "WhatsApp/Media/WhatsApp Images")

        file.listFiles().forEach {
            if (it.isFile) {
                lista_imagenes.add(archivo(File(it.path).name, (File(it.path).lastModified() / 1000).toString(), it.path, File(it.path).length().toDouble()))
            }
        }

        if (lista_imagenes.size == 0) {
            tamaño = "0,00"
            cantidad = 0
        } else {
            tamaño = String.format("%.2f", lista_imagenes.sumByDouble { it.tamaño } / (1024 * 1024 * 1024))
            cantidad = lista_imagenes.size
        }

        lista_archivos_total.add(lista_principal("Whatsapp Imagenes", cantidad.toString(), tamaño))


        supportFragmentManager
                .beginTransaction()
                .add(R.id.frame, pantalla_inicial(), "")
                .addToBackStack(null)
                .commit()
    }

    fun validar_permisos() {

        if (android.os.Build.VERSION.SDK_INT >= 23) {
            var permisos: ArrayList<String> = arrayListOf()


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permisos.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permisos.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }

            if (permisos.size > 0) {
                val arreglo = arrayOfNulls<String>(permisos.size)
                permisos.toArray(arreglo)
                this.requestPermissions(arreglo, 1)
            } else {
                programa()
            }


        } else {
            programa()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        var boolean = true

        for (result in grantResults) {
            if (result != 0) {
                boolean = false
            }
        }

        if (boolean) {
            programa()
        } else {
            validar_permisos()
        }
    }

    override fun onBackPressed() {
        if (this.supportFragmentManager.fragments.count() == 1) {
            finish()
        } else {
            supportFragmentManager.popBackStack()
        }
    }
}
