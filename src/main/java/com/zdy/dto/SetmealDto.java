package com.zdy.dto;

import com.zdy.domain.Dish;
import com.zdy.domain.DishFlavor;
import com.zdy.domain.Setmeal;
import com.zdy.domain.SetmealDish;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class SetmealDto extends Setmeal implements Serializable {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
