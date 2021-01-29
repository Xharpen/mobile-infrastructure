package com.xhlab.multiplatform.domain.worker

import com.xhlab.multiplatform.domain.MediatorUseCase

interface MediatorWorkable<in Params, Result, U : MediatorUseCase<Params, Result>> :
    WorkableBase<Params, Result>