package com.example.wanderlust.data.local

import android.content.Context
import androidx.room.Room

object DbProvider {
    @Volatile
    private var database: WanderlustDatabase? = null

    fun init(context: Context) {
        if (database == null) {
            synchronized(this) {
                if (database == null) {
                    database = Room.databaseBuilder(
                        context.applicationContext,
                        WanderlustDatabase::class.java,
                        "wanderlust.db",
                    ).fallbackToDestructiveMigration().build()
                }
            }
        }
    }

    fun db(): WanderlustDatabase =
        database ?: error("DbProvider is not initialized. Call DbProvider.init(context) first.")
}
