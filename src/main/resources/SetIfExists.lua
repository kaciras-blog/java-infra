if redis.call('HEXISTS', KEYS[1], ARGV[1]) == 0 then
	return 0;
else
	redis.call('HSET', KEYS[1], ARGV[1], ARGV[2])
	return 1
end
