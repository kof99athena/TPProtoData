package com.anehta.tpprotodata

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object UserDataSerializer : Serializer<MyUserData> {
    override val defaultValue: MyUserData = MyUserData.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): MyUserData {
        try {
            return MyUserData.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: MyUserData, output: OutputStream) = t.writeTo(output)

    val Context.settingsUserData: DataStore<MyUserData> by dataStore(
        fileName = "userdata.pb", //로컬에 저장될 protobuf 파일명
        serializer = UserDataSerializer
    )
}
