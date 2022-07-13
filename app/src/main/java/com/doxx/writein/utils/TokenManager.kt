package com.doxx.writein.utils

import android.content.Context
import com.doxx.writein.utils.Constants.PREFS_TOKEN_FILE
import com.doxx.writein.utils.Constants.USER_TOKEN
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TokenManager @Inject constructor(@ApplicationContext context: Context) {
    private var prefs = context.getSharedPreferences(PREFS_TOKEN_FILE,Context.MODE_PRIVATE)
    fun saveToken(token:String){
        val editor = prefs.edit().apply{
            putString(USER_TOKEN,token)
            apply()
        }

    }
    fun getToken():String?{
        return prefs.getString(USER_TOKEN,null)
    }
}