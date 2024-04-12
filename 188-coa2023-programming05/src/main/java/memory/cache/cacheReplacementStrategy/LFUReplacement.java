package memory.cache.cacheReplacementStrategy;


import memory.Memory;
import memory.cache.Cache;

/**
 * TODO 最近不经常使用算法
 */
public class LFUReplacement implements ReplacementStrategy {

    @Override
    public void hit(int rowNO) {
        Cache.getCache().addVisited(rowNO);
    }

    @Override
    public int replace(int start, int end, char[] addrTag, byte[] input) {
        Cache cache = Cache.getCache();
        int count = start;
        for (int i = start; i < end; i++){
            if (!cache.isValid(i)) {
                count = i;
                break;
            }
            if (cache.getVisited(i) < cache.getVisited(count)){
                    count = i;
            }
        }
        if (Cache.isWriteBack){
            if (cache.isDirty(count) && cache.isValid(count)){
                Memory.getMemory().write(cache.getPAddr(count),Cache.LINE_SIZE_B,cache.getData(count));
            }
        }
        cache.update(count,addrTag,input);
        return count;
    }

}
