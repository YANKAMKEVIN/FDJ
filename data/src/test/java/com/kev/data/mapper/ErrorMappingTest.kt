package com.kev.data.mapper

import com.kev.data.util.toDomainError
import com.kev.domain.util.DomainError
import org.junit.Test

class ErrorMappingTest {
    @Test
    fun io_maps_to_network() {
        val err = java.io.IOException("boom").toDomainError()
        assert(err == DomainError.Network)
    }

    @Test
    fun http_404_maps_to_notfound() {
        val err = retrofit2.HttpException(
            retrofit2.Response.error<String>(
                404,
                okhttp3.ResponseBody.create(null, "")
            )
        ).toDomainError()
        assert(err == DomainError.NotFound)
    }

    @Test
    fun http_401_maps_to_unauthorized() {
        val err = retrofit2.HttpException(
            retrofit2.Response.error<String>(
                401,
                okhttp3.ResponseBody.create(null, "")
            )
        ).toDomainError()
        assert(err == DomainError.Unauthorized)
    }
}
