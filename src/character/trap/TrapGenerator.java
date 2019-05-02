package character.trap;

public class TrapGenerator {
    private static final int TRAP_NORMAL = 0;
    private static final int TRAP_RUNNING = 1;
    private static final int TRAP_DANCING = 2;
    private static final int TRAP_STONE = 3;
    private static final int TRAP_SPRING = 4;
//    private static final int TRAP_FRAGMENT = 5;
    private static final int[] TRAP = {TRAP_NORMAL, TRAP_RUNNING,
                    TRAP_DANCING, TRAP_STONE,
                    TRAP_SPRING};

    private static TrapGenerator trapGenerator;

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
//            case TRAP_FRAGMENT:
//                return new FragmentTrap();
        }
        return null;
    }

    // 隨機生成器
    public Trap genTrap(){
        // 生成機制：
        //  先選擇哪種陷阱
        //  選擇後再判定有無過此機制的生成機率
        int random = (int)(Math.random()*TRAP.length);
        int rate = (int)(Math.random()*100);
        switch (random){
            case TRAP_RUNNING:
                if(rate > RunningTrap.generationRate){
                    return new RunningTrap(2);
                }
                break;
            case TRAP_DANCING:
                if(rate > DancingTrap.generationRate){
                    return new DancingTrap();
                }
                break;
            case TRAP_STONE:
                if(rate > StoneTrap.generationRate){
                    return new StoneTrap();
                }
                break;
            case TRAP_SPRING:
                if(rate > SpringTrap.generationRate){
                    return new SpringTrap();
                }
                break;
//            case TRAP_FRAGMENT:
//                if(rate > FragmentTrap.generationRate){
//                    return new FragmentTrap();
//                }
//                break;
            default:
                break;
        }
        // 若生成失敗，固定生成基礎地板
        return new NormalTrap();
    }
}
