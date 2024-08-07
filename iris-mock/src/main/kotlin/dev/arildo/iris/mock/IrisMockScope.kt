package dev.arildo.iris.mock

import dev.arildo.iris.mock.dsl.IrisMockDslMarker
import dev.arildo.iris.mock.util.createBlankResponse
import dev.arildo.iris.mock.util.isUsingIrisMockPlugin
import dev.arildo.iris.mock.util.log
import okhttp3.Interceptor
import okhttp3.Response

@IrisMockDslMarker
class IrisMockScope internal constructor(internal val chain: Interceptor.Chain) {

    internal fun build(): Response {
        return if (isUsingIrisMockPlugin()) {
            /* when using iris-mock plugin to auto-inject interceptors, then return
             * a blank response (as the Interceptor interface requires one).
             * The real processing will take place on [IrisMockWrapper],
             * considering all annotated interceptors
             */
            createBlankResponse(chain)
        } else {
            /* otherwise, return the result of processing directly, since the interceptor
             * implementation will be added manually to the OkHttp builder
             */
            ModifierProcessor.process(chain).also { response ->
                if (IrisMock.enableLogs) response.log()
            }
        }
    }
}
