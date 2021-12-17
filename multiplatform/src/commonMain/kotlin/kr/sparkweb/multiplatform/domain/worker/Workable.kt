package kr.sparkweb.multiplatform.domain.worker

import kr.sparkweb.multiplatform.domain.UseCase

interface Workable<in Params, Result, U : UseCase<Params, Result>> : WorkableBase<Params, Result>
