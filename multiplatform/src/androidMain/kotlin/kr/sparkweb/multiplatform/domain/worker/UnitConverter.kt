package kr.sparkweb.multiplatform.domain.worker

class UnitConverter : DataConverter<Unit> {
    override fun convert(from: Unit?): Unit? = null
    override fun convertBack(from: Any?): Unit = Unit
}
