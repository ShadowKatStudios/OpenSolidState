local fs = require("filesystem")

local arg = {...}

local romfs = io.open(arg[2], "wb")
romfs:write("romfs",string.char(1,0))
local paths = {}

local function add_dir(path, rpath)
  for e in fs.list(path) do
    if (e == "." or e == "..") then
      goto continue
    end
    if (e:match("/$")) then
      add_dir(path.."/"..e:sub(1, #e-1), rpath.."/"..e:sub(1, #e-1))
    else
      paths[#paths+1] = {
        path.."/"..e,
        rpath.."/"..e,
        fs.size(path.."/"..e),
        ((e:match("%.lua$") and "  x") or "  -")
      }
    end
    ::continue::
  end
end

add_dir(arg[1], "")

-- Write files to romfs
for i=1, #paths do
  romfs:write(string.char(#paths[i][2]),paths[i][2])
  romfs:write(string.char(paths[i][3] & 0xFF, paths[i][3] >> 8))
  romfs:write(paths[i][4]:sub(3, 3))
  local f = io.open(paths[i][1], "rb")
  local d = f:read("*a")
  romfs:write(d)
  f:close()
end
romfs:write(string.char(10), "TRAILER!!!")
romfs:write(string.char(0,0,0))
romfs:close()