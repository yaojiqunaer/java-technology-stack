-- Lua script for reducing inventory and adding points atomically
-- KEYS[1] = stock key
-- KEYS[2] = points key
-- ARGV[1] = quantity to reduce
-- ARGV[2] = points to add
-- ARGV[3] = request identifier for deduplication

local stock_key = KEYS[1]
local points_key = KEYS[2]
local qty_to_reduce = tonumber(ARGV[1])
local points_to_add = tonumber(ARGV[2])
local request_id = ARGV[3]

-- Validate inputs
if not qty_to_reduce or qty_to_reduce <= 0 then
    return "0:INVALID_QUANTITY"
end

if not points_to_add then
    return "0:INVALID_POINTS"
end

-- Check if this request has already been processed (deduplication)
local processed_key = "processed:" .. request_id
if redis.call('EXISTS', processed_key) == 1 then
    return "0:DUPLICATE_REQUEST"
end

-- Get current stock (returns false if key doesn't exist)
local current_stock_reply = redis.call('GET', stock_key)
local current_stock = 0
if current_stock_reply ~= false then
    current_stock = tonumber(current_stock_reply)
end

-- Check if we have enough stock
if current_stock < qty_to_reduce then
    return "0:INSUFFICIENT_STOCK"
end

-- Reduce stock
redis.call('DECRBY', stock_key, qty_to_reduce)

-- Get current points (returns false if key doesn't exist)
local current_points_reply = redis.call('GET', points_key)
local current_points = 0
if current_points_reply ~= false then
    current_points = tonumber(current_points_reply)
end

-- If points to add is negative, check if user has enough points
if points_to_add < 0 and current_points < math.abs(points_to_add) then
    -- Rollback stock reduction
    redis.call('INCRBY', stock_key, qty_to_reduce)
    return "0:INSUFFICIENT_POINTS"
end

-- Add points (can be positive or negative)
redis.call('INCRBY', points_key, points_to_add)

-- Mark this request as processed with expiration (1 hour)
redis.call('SETEX', processed_key, 3600, 1)

return "1:SUCCESS"