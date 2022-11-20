package jumbo5337.carserivce.model


sealed class Response<T : Identified<*>> {
    abstract val code: Int
    abstract val message: String
    abstract val entity: T
}

data class Success<T : Identified<*>>(
    override val entity: T
) : Response<T>() {
    override val code: Int = 0
    override val message: String = "OK"
}

data class Duplicate<T : Identified<*>>(
    override val entity: T
) : Response<T>() {
    override val code: Int = 422
    override val message: String = "${javaClass.simpleName}=[${entity.id}] already exists"
}