package com.xhlab.multiplatform.domain.worker

import com.xhlab.multiplatform.domain.UseCase

interface Workable<in Params, Result, U : UseCase<Params, Result>> : WorkableBase<Params, Result>