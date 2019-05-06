package character.trap;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

public class TrapGenerator {
    // 破碎地板暫時移除
    private static final int TRAP_NORMAL = 0;
    private static final int TRAP_RUNNING = 1;
    private static final int TRAP_DANCING = 2;
    private static final int TRAP_STONE = 3;
    private static final int TRAP_SPRING = 4;
//    private static final int TRAP_FRAGMENT = 5;
    private static final int TRAP_FLASH = 5;
    private static final int[] TRAP = {TRAP_NORMAL, TRAP_RUNNING,
                    TRAP_DANCING, TRAP_STONE,
                    TRAP_SPRING, TRAP_FLASH};

    // 陷阱生成機率
    private int genRate_RunningTrap;
    private int genRate_DancingTrap;
    private int genRate_SpringTrap;
    private int genRate_StoneTrap;
    private int genRate_FragmentTrap;
    private int getGenRate_FlashTrap;

    private static TrapGenerator trapGenerator;

    private TrapGenerator(){
        // 初始生成機率
        genRate_RunningTrap = 20;
        genRate_DancingTrap = 50;
        genRate_SpringTrap = 25;
        genRate_StoneTrap = 60;
        genRate_FragmentTrap = 30;
        getGenRate_FlashTrap = 50;
    }

    public static TrapGenerator getInstance(){
        if (trapGenerator == null){
            trapGenerator = new TrapGenerator();
        }
        return trapGenerator;
    }

    // 測試用生成器
    public Trap genSpecificTrap(int trapCode){
        switch (trapCode){
            case TRAP_NORMAL:
                return new NormalTrap();
            case TRAP_RUNNING:
                return new RunningTrap(2);
            case TRAP_DANCING:
                return new DancingTrap();
            case TRAP_STONE:
                return new StoneTrap();
            case TRAP_SPRING:
                return new SpringTrap();
            case TRAP_FLASH:
                return new FlashTrap();
//            case TRAP_FRAGMENT:
//                return new FragmentTrap();
        }
        return null;
    }

    // 隨機生成器，傳入層數，調整生成機率
    public Trap genTrap(int layer){
        generationUpdater(layer);
        // 生成機制：
        //  先選擇哪種陷阱
        //  選擇後再判定有無過此機制的生成機率
        int random = (int)(Math.random()*TRAP.length);
        int rate = (int)(Math.random()*100);
        switch (random){
            case TRAP_RUNNING:
                if(rate < genRate_RunningTrap){
                    return new RunningTrap(1);
                }
                break;
            case TRAP_DANCING:
                if(rate < genRate_DancingTrap){
                    return new DancingTrap();
                }
                break;
            case TRAP_STONE:
                if(rate < genRate_StoneTrap){
                    return new StoneTrap();
                }
                break;
            case TRAP_SPRING:
                if(rate < genRate_SpringTrap){
                    return new SpringTrap();
                }
                break;
//            case TRAP_FRAGMENT:
//                if(rate > FragmentTrap.generationRate){
//                    return new FragmentTrap();
//                }
//                break;
            case TRAP_FLASH:
                if(rate < getGenRate_FlashTrap){
                    System.out.println("gen");
                    return new FlashTrap();
                }
                break;
            default:
                break;
        }
        // 若生成失敗，固定生成基礎地板
        return new NormalTrap();
    }

    // 陷阱生成機率更新器，傳入層數調整機率
    private void generationUpdater(int layer){
        if (layer <= 5){
            genRate_RunningTrap = 20;
            genRate_DancingTrap = 50;
            genRate_SpringTrap = 25;
            genRate_StoneTrap = 60;
            genRate_FragmentTrap = 30;
            getGenRate_FlashTrap = 100;

        }
        if (layer > 5 && layer <= 20){
            genRate_RunningTrap = 0;
            genRate_DancingTrap = 0;
            genRate_SpringTrap = 100;
            genRate_StoneTrap = 0;
            genRate_FragmentTrap = 0;
        }
    }
}
