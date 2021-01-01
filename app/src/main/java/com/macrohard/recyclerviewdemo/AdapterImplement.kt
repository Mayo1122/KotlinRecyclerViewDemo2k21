package com.macrohard.recyclerviewdemo

import androidx.databinding.ObservableField
import com.macrohard.recyclerviewlib.GenericAdapter


public class AdapterImplement(val listString: List<String>): GenericAdapter<List<ObservableField<String>>>(){
    fun getVarId(): Int {
        return /*BR.item*/1
    }

    override fun getItemCount(): Int {
        return listString.size
    }

    fun getLayoutIdForPosition(position: Int): Int {
        return /*R.layout.item_adapter*/1
    }

    fun getObjForPosition(position: Int): Any {
        return ObservableField(listString[position])
    }

    override fun update(items: ArrayList<List<ObservableField<String>>>) {
        //implement
    }

}