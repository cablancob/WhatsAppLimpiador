package com.carlos.whatsapplimpiador

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.Environment
import android.widget.RemoteViews
import java.io.File
import java.io.FileFilter
import java.util.*

class limpiador_automatico : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action.equals("com.carlos.limpiadorautomatico")) {

            if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY).toString().equals("0")) {

                var lista_videos: MutableList<archivo> = mutableListOf()
                var lista_imagenes: MutableList<archivo> = mutableListOf()
                var cantidad_videos = 0
                var cantidad_Imagenes = 0
                var mensaje = "Se han borrando en total: "
                var cantidad_semanas = -2

                lista_videos.clear()
                lista_imagenes.clear()

                var file = File(Environment.getExternalStorageDirectory().absolutePath, "WhatsApp/Media/WhatsApp Images")

                file.listFiles(object : FileFilter {
                    override fun accept(pathname: File?): Boolean {
                        var fecha = Calendar.getInstance()
                        fecha.add(Calendar.WEEK_OF_YEAR, cantidad_semanas)
                        var resultado = false
                        if (pathname!!.isFile) {
                            resultado = pathname.lastModified() / 1000 <= fecha.timeInMillis / 1000
                        }
                        return resultado
                    }
                }).forEach {
                    if (it.isFile) {
                        lista_imagenes.add(archivo(File(it.path).name, (File(it.path).lastModified() / 1000).toString(), it.path, File(it.path).length().toDouble()))
                    }
                }

                cantidad_Imagenes = lista_imagenes.size

                lista_imagenes.forEach {
                    File(it.path).delete()
                }

                file = File(Environment.getExternalStorageDirectory().absolutePath, "WhatsApp/Media/WhatsApp Video")

                file.listFiles(object : FileFilter {
                    override fun accept(pathname: File?): Boolean {
                        var fecha = Calendar.getInstance()
                        fecha.add(Calendar.WEEK_OF_YEAR, cantidad_semanas)
                        var resultado = false
                        if (pathname!!.isFile) {
                            resultado = pathname.lastModified() / 1000 <= fecha.timeInMillis / 1000
                        }
                        return resultado
                    }
                }).forEach {
                    if (it.isFile) {
                        lista_videos.add(archivo(File(it.path).name, (File(it.path).lastModified() / 1000).toString(), it.path, File(it.path).length().toDouble()))
                    }
                }

                cantidad_videos = lista_videos.size

                lista_videos.forEach {
                    File(it.path).delete()
                }


                if ((cantidad_Imagenes > 0) and (cantidad_videos == 0)) {
                    mensaje = mensaje + "${cantidad_Imagenes} imagenes"
                }

                if ((cantidad_Imagenes == 0) and (cantidad_videos > 0)) {
                    mensaje = mensaje + "${cantidad_videos} videos"
                }

                if ((cantidad_Imagenes > 0) and (cantidad_videos > 0)) {
                    mensaje = mensaje + "${cantidad_videos} videos y ${cantidad_Imagenes} imagenes"
                }

                if ((cantidad_Imagenes > 0) or (cantidad_videos > 0)) {
                    Notificaciones(context, mensaje)
                }

                if ((cantidad_Imagenes == 0) or (cantidad_videos == 0)) {
                    mensaje = "No hay archivos de mas de ${cantidad_semanas * -1} semanas para borrar "
                    Notificaciones(context, mensaje)
                }

            }
        }

    }

    fun Notificaciones(context: Context, mensaje: String) {
        lateinit var notificationManager: NotificationManager
        lateinit var notificationChannel: NotificationChannel
        lateinit var builder: Notification.Builder
        var channelId = "com.carlos.whatsapplimpiador"
        var description = "Notificacion de WhatsappLimpiador"
        val intent = Intent(context, actividad_principal::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val contentView = RemoteViews("com.carlos.whatsapplimpiador", R.layout.notificacion_layout)
        contentView.setTextViewText(R.id.NotificacionContenido, mensaje)
        contentView.setTextViewText(R.id.NotificacionHora, "- Hora: ${Calendar.getInstance().get(Calendar.HOUR_OF_DAY).toString().padStart(2, '0')}:${Calendar.getInstance().get(Calendar.MINUTE).toString().padStart(2, '0')}")



        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)




            builder = Notification.Builder(context, channelId)
                    .setContent(contentView)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                    .setContentIntent(pendingIntent)

        } else {
            builder = Notification.Builder(context)
                    .setContent(contentView)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                    .setContentIntent(pendingIntent)
        }

        notificationManager.notify(1234, builder.build())
    }
}
