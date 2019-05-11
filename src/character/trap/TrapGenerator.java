package character.trap;

public class TrapGenerator {
    // 破碎地板暫時移除
    public static final int TRAP_NORMAL = 0;
    public static final int TRAP_RUNNING = 1;
    public static final int TRAP_DANCING = 2;
    public static final int TRAP_STONE = 3;
    public static final int TRAP_SPRING = 4;
//    public static final int TRAP_FRAGMENT = 5;
    public static final int TRAP_FLASH = 6;
    public static final int TRAP_FLIPPING = 7;
    public static final int TRAP_DARKNESS = 5;
    private static final int[] TRAP = {TRAP_NORMAL, TRAP_RUNNING,
                    TRAP_DANCING, TRAP_STONE, // 破碎地板移除
                    TRAP_SPRING, TRAP_FLASH, TRAP_FLIPPING, TRAP_DARKNESS};

    // 陷阱生成機率
    private int genRate_RunningTrap;
    private int genRate_DancingTrap;
    private int genRate_SpringTrap;
    private int genRate_StoneTrap;
    private int genRate_FragmentTrap;
    private int genRate_FlashTrap;
    private int genRate_FlippingTrap;
    private int genRate_DarknessTrap;

    private static TrapGenerator trapGenerator;

    private TrapGenerator(){
        // 初始生成機率
        genRate_RunningTrap = 20;
        genRate_DancingTrap = 50;
        genRate_SpringTrap = 25;
        genRate_StoneTrap = 60;
//        genRate_FragmentTrap = 30;
        genRate_FlashTrap = 50;
        genRate_FlippingTrap = 60;
        genRate_DarknessTrap = 70;
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
            case TRAP_FLIPPING:
                return new FlippingTrap();
            case TRAP_DARKNESS:
                return new DarknessTrap();
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
                    return new RunningTrap(2);
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
//                if(rate > genRate_FragmentTrap){
//                    return new FragmentTrap();
//                }
            case TRAP_FLASH:
                if(rate < genRate_FlashTrap){
                    return new FlashTrap();
                }
                break;
            case TRAP_FLIPPING:
                if(rate < genRate_FlippingTrap){
                    return new FlippingTrap();
                }
                break;
            case TRAP_DARKNESS:
                if (rate < genRate_DarknessTrap){
                    return new DarknessTrap();
                }
            default:
                break;
        }
        // 若生成失敗，固定生成基礎地板
        return new NormalTrap();
    }

    // 陷阱生成機率更新器，傳入層數調整機率
    private void generationUpdater(int layer){
        if (layer <= 5){
            genRate_RunningTrap = 30;
            genRate_DancingTrap = 20;
            genRate_SpringTrap = 25;
            genRate_StoneTrap = 40;
            genRate_FlashTrap = 20;
            genRate_FlippingTrap = 40;
            genRate_DarknessTrap = 20;
        }
        if (layer > 5){
            System.out.println("layer > 5");
            genRate_RunningTrap = 30 + 5 * layer/5;
            genRate_DancingTrap = 20 + 5 * layer/5;
            genRate_SpringTrap = 25 + 5 * layer/5;
            genRate_StoneTrap = 40 + 5 * layer/5;
            genRate_FlashTrap = 20 + 5 * layer/5;
            genRate_FlippingTrap = 40 + 5 * layer/5;
            genRate_DarknessTrap = 20 + 5 * layer/5;
        }
    }
}
