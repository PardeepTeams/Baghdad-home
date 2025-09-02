package com.baghdadhomes.Models

import android.graphics.drawable.Drawable

data class PropertySubTypesModel (
    var image : Drawable?= null,
    var name : String ?= null,
    var value : String ?= null,
    var isSelected : Boolean ?= false,
)