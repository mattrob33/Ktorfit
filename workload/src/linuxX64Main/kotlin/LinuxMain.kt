import com.example.api.JsonPlaceHolderApi
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.adapter.FlowResponseConverter
import de.jensklingenberg.ktorfit.create
import io.ktor.client.*
import kotlinx.coroutines.runBlocking

fun main() {

    val linuxKtorfit = Ktorfit(JsonPlaceHolderApi.baseUrl, HttpClient())
    linuxKtorfit.addResponseConverter(FlowResponseConverter())
    val api =linuxKtorfit.create<JsonPlaceHolderApi>()
    runBlocking {
        api.getPosts().collect{
            println(it)
        }
    }

    println("ddd")
}
