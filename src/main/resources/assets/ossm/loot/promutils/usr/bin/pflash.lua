local fin = ...
local prom = require("component").ossm_prom
prom.erase()

local data = {}

local last_data = ""

local f = io.open(fin, "rb")
io.stdout:write("Loading ROMFS")
while last_data do
  io.stdout:write(".")
  last_data = f:read(512)
  if (last_data) then
    data[#data+1] = last_data
  end
end

print(#data.." blocks.")
if (#data > prom.numBlocks()) then
  io.stderr:write("Not enough storage. ("..#data.." > "..(prom.numBlocks()//1)..")")
  return
end
os.sleep(0.01)
f:close()
io.stdout:write("Writing block ")
for i=1, #data do
  if (data[i] == nil) then print("Error") return end
  io.stdout:write(i.."...")
  prom.blockWrite(i, data[i])
end
print("Done.")