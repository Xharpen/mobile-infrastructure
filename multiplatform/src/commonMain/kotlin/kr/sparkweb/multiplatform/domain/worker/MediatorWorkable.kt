package kr.sparkweb.multiplatform.domain.worker

import kr.sparkweb.multiplatform.domain.MediatorUseCase

interface MediatorWorkable<in Params, Result, U : MediatorUseCase<Params, Result>> :
    WorkableBase<Params, Result>
