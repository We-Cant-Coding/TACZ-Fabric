package com.tacz.guns.api.client.animation.statemachine;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.*;

public class LuaAnimationState<T extends AnimationStateContext> implements AnimationState<LuaContextWrapper<T>> {
    private final @NotNull LuaTable luaTable;
    private final @Nullable LuaFunction updateFunction;
    private final @Nullable LuaFunction enterFunction;
    private final @Nullable LuaFunction exitFunction;
    private final @Nullable LuaFunction transitionFunction;

    /**
     * 此方法用于通过 lua 脚本生成状态。不应该被直接调用，而是通过工厂生成。
     *
     * @param luaTable 包含各个函数的表
     * @see LuaStateMachineFactory
     */
    LuaAnimationState(@NotNull LuaTable luaTable) {
        this.luaTable = luaTable;
        this.updateFunction = checkLuaFunction("update");
        this.enterFunction = checkLuaFunction("entry");
        this.exitFunction = checkLuaFunction("exit");
        this.transitionFunction = checkLuaFunction("transition");
    }

    @Override
    public void update(LuaContextWrapper<T> context) {
        if (updateFunction != null) {
            updateFunction.call(context.getLuaContext());
        }
    }

    @Override
    public void entryAction(LuaContextWrapper<T> context) {
        if (enterFunction != null) {
            enterFunction.call(context.getLuaContext());
        }
    }

    @Override
    public void exitAction(LuaContextWrapper<T> context) {
        if (exitFunction != null) {
            exitFunction.call(context.getLuaContext());
        }
    }

    @Override
    public AnimationState<LuaContextWrapper<T>> transition(LuaContextWrapper<T> context, String condition) {
        if (transitionFunction != null) {
            LuaString conditionToLua = LuaString.valueOf(condition);
            LuaValue nextStateTable = transitionFunction.call(context.getLuaContext(), conditionToLua);
            if (nextStateTable.istable()) {
                return new LuaAnimationState<>((LuaTable) nextStateTable);
            } else if (nextStateTable.isnil()) {
                return null;
            }
            throw new LuaError("the return of function 'transition' must be table or nil");
        }
        return null;
    }

    private LuaFunction checkLuaFunction(String funcName) {
        LuaValue value = luaTable.get(funcName);
        if (value.isfunction()) {
            return (LuaFunction) value;
        } else if (value.isnil()) {
            return null;
        }
        throw new LuaError("the type of field '" + funcName + "' must be function or nil");
    }
}
